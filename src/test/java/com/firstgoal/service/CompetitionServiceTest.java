package com.firstgoal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstgoal.cache.CompetitionRepository;
import com.firstgoal.messaging.MessagePublisher;
import com.firstgoal.service.exceptions.CompetitionAlreadyStartedException;
import com.firstgoal.service.exceptions.CompetitionNotFoundException;
import com.firstgoal.service.exceptions.CompetitionNotLiveException;
import com.firstgoal.service.exceptions.InvalidParticipantNumbersException;
import com.firstgoal.web.Competition;
import com.firstgoal.web.Participant;
import com.firstgoal.web.Player;
import com.mashape.unirest.http.JsonNode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceTest {

    @Mock
    BBCClient bbcClient;

    @Mock
    CompetitionRepository competitionRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    MessagePublisher messagePublisher;

    CompetitionService competitionService;

    @BeforeEach
    public void init() {
        initMocks(this);
        competitionService = new CompetitionService(objectMapper, competitionRepository, bbcClient, messagePublisher);
    }

    @SneakyThrows
    @Test
    void createCompetiton() {
        JsonNode response = Mockito.mock(JsonNode.class);
        Competition competition = Mockito.mock(Competition.class);

        when(bbcClient.get(anyString())).thenReturn(response);
        when(response.toString()).thenReturn("string");
        when(objectMapper.readValue(anyString(), eq(Competition.class))).thenReturn(competition);
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        doNothing().when(competition).setOwner(anyString());
        doNothing().when(competition).setEventUrl(anyString());

        competitionService.createCompetition("lineupUrl", "eventUrl", "owner");

        verify(bbcClient,  times(1)).get(eq("lineupUrl"));
        verify(objectMapper,  times(1)).readValue(anyString(), eq(Competition.class));
        verify(competitionRepository,  times(1)).save(eq(competition));
        verify(competition,  times(1)).setOwner(eq("owner"));
        verify(competition,  times(1)).setEventUrl(eq("eventUrl"));
    }

    @Test
    void addSelectionsToCompetition() {

        Competition competition = Mockito.mock(Competition.class);

        doNothing().when(competition).setSelections(any());
        doNothing().when(competition).setLive(anyBoolean());
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        competitionService.addSelectionsToCompetition("id", new Player[]{});

        verify(competition, times(1)).setSelections(any());
        verify(competition, times(1)).setLive(eq(true));
        verify(competitionRepository,  times(1)).findById(eq("id"));
        verify(competitionRepository,  times(1)).save(eq(competition));
    }

    @Test
    void getCompetition() {
        Competition competition = Mockito.mock(Competition.class);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        competitionService.getCompetition("id");
        verify(competitionRepository,  times(1)).findById(eq("id"));
    }

    @Test
    void getCompetition_NotFound() {
        Competition competition = Mockito.mock(Competition.class);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.ofNullable(null));
        assertThrows(CompetitionNotFoundException.class, () -> {competitionService.getCompetition("id");});
    }

    @Test
    void addParticipant() {
        Competition competition = Mockito.mock(Competition.class);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("1"));
        participants.add(new Participant("2"));
        participants.add(new Participant("3"));

        when(competition.getParticipants()).thenReturn(participants);
        when(competition.isLive()).thenReturn(true);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        competitionService.addParticipant("id", "name");

        verify(competition, times(2)).getParticipants();
        verify(competition, times(1)).isLive();
        verify(competitionRepository,  times(1)).findById(eq("id"));
        verify(competitionRepository,  times(1)).save(eq(competition));

        assertEquals(participants.size(), 4);

    }

    @Test
    void addParticipant_FailIfStarted() {
        Competition competition = Mockito.mock(Competition.class);

        when(competition.isStarted()).thenReturn(true);
        when(competition.isLive()).thenReturn(true);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));

        assertThrows(CompetitionAlreadyStartedException.class, () -> {competitionService.addParticipant("id", "name");});
    }

    @Test
    void addParticipant_FailIfNotLive() {
        Competition competition = Mockito.mock(Competition.class);

        when(competition.isLive()).thenReturn(false);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));

        assertThrows(CompetitionNotLiveException.class, () -> {competitionService.addParticipant("id", "name");});
    }

    @Test
    void deleteCompetition() {
        Competition competition = Mockito.mock(Competition.class);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        doNothing().when(competitionRepository).deleteById(anyString());

        competitionService.deleteCompetition("id");
        verify(competitionRepository,  times(1)).findById(eq("id"));
        verify(competitionRepository,  times(1)).deleteById(eq("id"));
    }

    @Test
    void deleteCompetition_failIfStarted() {
        Competition competition = Mockito.mock(Competition.class);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        when(competition.isStarted()).thenReturn(true);

        assertThrows(CompetitionAlreadyStartedException.class, () -> {competitionService.deleteCompetition("id");});
    }

    @Test
    void removeParticipants() {
        Competition competition = Mockito.mock(Competition.class);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("1"));
        participants.add(new Participant("2"));
        participants.add(new Participant("3"));

        when(competition.getParticipants()).thenReturn(participants);
        when(competition.isLive()).thenReturn(true);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        competitionService.removeParticipants("id", new String[]{"1","2"});

        verify(competition, times(1)).getParticipants();
        verify(competition, times(1)).isLive();
        verify(competitionRepository,  times(1)).findById(eq("id"));
        verify(competitionRepository,  times(1)).save(eq(competition));

        assertEquals(1, participants.size());
    }

    @Test
    void removeParticipants_failIfStarted() {
        Competition competition = Mockito.mock(Competition.class);
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));
        when(competition.isStarted()).thenReturn(true);
        when(competition.isLive()).thenReturn(true);

        assertThrows(CompetitionAlreadyStartedException.class, () -> {competitionService.removeParticipants("id", new String[]{"1","2"});});
    }

    @Test
    public void startCompetition() {
        Competition competition = Mockito.mock(Competition.class);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("1"));
        participants.add(new Participant("2"));
        participants.add(new Participant("3"));

        Player[] players = new Player[21];

        for (int i = 0; i < 20; i++) {
            players[i] = new Player("pos", Integer.toString(i));
        }

        when(competition.getParticipants()).thenReturn(participants);
        when(competition.getSelections()).thenReturn(players);
        doNothing().when(competition).setStarted(anyBoolean());
        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));

        competitionService.startCompetition("id");

        participants.stream().forEach(
            participant -> {
                assertTrue(participant.getAssigments().size() == 7);
                //no other participant got a selection
                participant.getAssigments().stream().forEach(
                    assignment -> participants.stream()
                        .filter(p1 -> p1.getName() != participant.getName()).forEach(
                            p1 -> assertFalse(p1.getAssigments().contains(assignment))
                        )
                );
            }
        );
    }

    @Test
    public void startCompetition_failOnInvalidParticipantNumbers() {
        Competition competition = Mockito.mock(Competition.class);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("1"));
        participants.add(new Participant("2"));
        participants.add(new Participant("3"));

        Player[] players = new Player[] {new Player("pos", "1"), new Player("pos", "2")};

        when(competition.getParticipants()).thenReturn(participants);
        when(competition.getSelections()).thenReturn(players);

        when(competitionRepository.findById(anyString())).thenReturn(Optional.of(competition));

        Exception exception = assertThrows(InvalidParticipantNumbersException.class, () -> {
            competitionService.startCompetition("id");
        });
    }
}