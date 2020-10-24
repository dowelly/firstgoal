package com.firstgoal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstgoal.cache.CompetitionRepository;
import com.firstgoal.messaging.MessagePublisher;
import com.firstgoal.service.exceptions.CompetitionAlreadyStartedException;
import com.firstgoal.service.exceptions.CompetitionNotFoundException;
import com.firstgoal.service.exceptions.CompetitionNotLiveException;
import com.firstgoal.service.exceptions.InvalidParticipantNumbersException;
import com.firstgoal.service.exceptions.ParticipantExistsException;
import com.firstgoal.web.Competition;
import com.firstgoal.web.Participant;
import com.firstgoal.web.Player;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static com.firstgoal.config.Constants.COMPETITION_STARTED;


@Service
@Slf4j
public class CompetitionService {

    private final ObjectMapper objectMapper;

    private final CompetitionRepository competitionRepository;

    private final BBCClient bbcClient;

    private final MessagePublisher messagePublisher;

    @Autowired
    public CompetitionService(
        ObjectMapper objectMapper,
        CompetitionRepository competitionRepository,
        BBCClient bbcClient,
        MessagePublisher messagePublisher) {
        this.objectMapper = objectMapper;
        this.competitionRepository = competitionRepository;
        this.bbcClient = bbcClient;
        this.messagePublisher = messagePublisher;
    }

    @SneakyThrows
    public Competition createCompetition(String lineupUrl, String eventUrl, String owner) {
        Competition competition = objectMapper.readValue(bbcClient.get(lineupUrl).toString(), Competition.class);
        competition.setOwner(owner);
        competition.setEventUrl(eventUrl);
        competitionRepository.save(competition);
        log.info("Competition {} created, listening to {}", competition.getId(), eventUrl);
        return competition;
    }

    public synchronized Competition addSelectionsToCompetition(String id, Player[] selections) {
        Competition competition = getCompetition(id);
        competition.setSelections(selections);
        competition.setLive(true);
        competitionRepository.save(competition);
        log.info("{} added to competiton {}", selections, id);
        return competition;
    }

    public Competition getCompetition(String id) {
        return competitionRepository.findById(id).orElseThrow(CompetitionNotFoundException::new);
    }

    @SneakyThrows
    public synchronized Competition addParticipant(String id, String name) {
        Competition competition = getCompetition(id);
        if (!competition.isLive()) throw new CompetitionNotLiveException();
        if (competition.isStarted()) throw new CompetitionAlreadyStartedException();
        if (competition.getParticipants().contains(name)) throw new ParticipantExistsException();
        competition.getParticipants().add(new Participant(name));
        competitionRepository.save(competition);
        log.info("{} added to competition {}", name, id);
        return competition;
    }

    public void deleteCompetition(String id) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition == null) return;
        if (competition.isStarted()) throw new CompetitionAlreadyStartedException();
        competitionRepository.deleteById(id);
        log.info("competition {} deleted", id);
    }

    public synchronized void removeParticipants(String id, String[] participants) {
        Competition competition = getCompetition(id);
        if (!competition.isLive()) throw new CompetitionNotLiveException();
        if (competition.isStarted()) throw new CompetitionAlreadyStartedException();
        List<String> participantsToRemove = Arrays.asList(participants);
        competition.getParticipants().removeIf(participant -> participantsToRemove.contains(participant.getName()));
        competitionRepository.save(competition);
        log.info("{} removed from competiton {}", participants, id);
    }

    public synchronized Competition startCompetition(String id) {
        Competition competition = getCompetition(id);
        if (competition.getSelections().length % competition.getParticipants().size() != 0) throw new InvalidParticipantNumbersException();

        List<Player> selections = Arrays.asList(competition.getSelections().clone());

        Collections.shuffle(selections);

        Stack<Player> playerStack = new Stack<>();
        playerStack.addAll(selections);

        //round robin of participants until no more players
        int rounds = selections.size() / competition.getParticipants().size();
        for (int i = 0; i < rounds; i++) {
            competition.getParticipants().stream().forEach(
                participant -> {
                    participant.getAssigments().add(playerStack.pop());
                }
            );
        }
        log.info("Competition {} assignments are", competition.getId());
        log.info(competition.printAssignments());
        competition.setStarted(true);
        competitionRepository.save(competition);

        messagePublisher.publish(COMPETITION_STARTED + ":" + competition.getId());
        log.info("competition {} started", competition.getId());
        return competition;
    }

    public void updateScorer(String competitionId, Player scorer) {
        getCompetition(competitionId).setWinner(scorer);
    }
}
