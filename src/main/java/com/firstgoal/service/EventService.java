package com.firstgoal.service;

import com.firstgoal.cache.EventInProgressRepository;
import com.firstgoal.data.EventInProgress;
import com.firstgoal.web.Competition;
import com.firstgoal.web.Player;
import com.mashape.unirest.http.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

import static com.firstgoal.config.Constants.COMPETITION_STARTED;

@Service
@Slf4j

public class EventService implements MessageListener {

    private CompetitionService competitionService;
    private EventInProgressRepository eventInProgressRepository;
    private BBCClient bbcClient;

    @Autowired
    public EventService(
        CompetitionService competitionService,
        EventInProgressRepository eventInProgressRepository,
        BBCClient bbcClient) {
        this.competitionService = competitionService;
        this.eventInProgressRepository = eventInProgressRepository;
        this.bbcClient = bbcClient;
    }

    //Hack for now
    public void onMessage(Message message, byte[] pattern) {
        log.info(message.toString());

        String type = message.toString().split(":")[0];
        switch (type) {
            case COMPETITION_STARTED:
                createEventInProgress(message.toString().split(":")[1]);
                break;
            default:
        }
    }

    private void createEventInProgress(String id) {
        Competition competition = competitionService.getCompetition(id);

        log.info("competition {} started", id);

        EventInProgress eventInProgress = eventInProgressRepository
            .findById(competition.getEventUrl())
            .orElseGet(
                () -> {
                    ArrayList<String> competitions = new ArrayList<>();

                    log.info("event in progress registered, listening to url {}", competition.getEventUrl());

                    return EventInProgress.builder()
                        .eventUrl(competition.getEventUrl())
                        .competitions(competitions)
                        .build();
                });

        eventInProgress.getCompetitions().add(competition.getId());

        log.info("competitions interested in this event {}", eventInProgress.getCompetitions());

        eventInProgressRepository.save(eventInProgress);
    }

    @Scheduled(fixedDelay = 1000)
    public void pollEvents() {
        StreamSupport.stream(eventInProgressRepository.findAll().spliterator(), true)
            .parallel().forEach(this::pollEvent);
    }

    public void pollEvent(EventInProgress event) {
        if(event != null) {

            Player scorer = getScorer(bbcClient.get(event.getEventUrl()));
            if (scorer != null) {
                registerScorer(scorer, event);
            }
        }
    }

    private void registerScorer(Player scorer, EventInProgress event) {
        event.getCompetitions().forEach(
            competitionId -> competitionService.updateScorer(competitionId, scorer)
        );

        log.info("removing event {}", event.getEventUrl());
        eventInProgressRepository.delete(event);
    }

    private Player getScorer(JsonNode result) {
        //check home team
        JSONObject homeTeam = result.getObject().getJSONObject("event").getJSONObject("homeTeam");
        String homeTeamName = homeTeam.getJSONObject("name").getString("full");
        JSONObject awayTeam = result.getObject().getJSONObject("event").getJSONObject("awayTeam");
        String awayTeamName = awayTeam.getJSONObject("name").getString("full");
        String competiton = result.getObject().getJSONObject("event").getString("competitionNameString");
        Integer mins = result.getObject().getJSONObject("event").getInt("minutesElapsed");
        JSONArray homeTeamActions = homeTeam.getJSONArray("playerActions");
        JSONArray awayTeamActions = awayTeam.getJSONArray("playerActions");

        String homeTeamFirstScorer = null;
        Integer homeTeamFirstScorerTime = null;

        if (homeTeamActions.length()==0) {
            log.info("no scorer for {} vs {} in {} at {} mins", homeTeamName, awayTeamName, competiton, mins);
        } else {
            homeTeamFirstScorer = getScorerName(homeTeamActions);
            homeTeamFirstScorerTime = getScorerTime(homeTeamActions);

            log.info("{} scored for {} at {} mins vs {} in {}", homeTeamFirstScorer, homeTeamName, homeTeamFirstScorerTime, awayTeamName, competiton);

            if (awayTeamActions.length() == 0) {
                log.info("no scorer for {} vs {} in {} at {} mins", awayTeamName, homeTeamName, competiton, mins);

                return Player.builder().name(homeTeamFirstScorer).build();
            } else {
                String awayTeamScorer = getScorerName(awayTeamActions);
                Integer awayTeamScorerTime = getScorerTime(awayTeamActions);

                log.info("{} scored for {} at {} mins vs {} in {}", awayTeamScorer, awayTeamName, awayTeamScorerTime, homeTeamName, competiton);
                if (homeTeamFirstScorerTime < awayTeamScorerTime) {
                    log.info("{} got the first goal", homeTeamFirstScorer);
                    return Player.builder().name(homeTeamFirstScorer).build();
                } else {
                    log.info("{} got the first goal", awayTeamScorer);
                    return Player.builder().name(awayTeamScorer).build();
                }
            }
        }

        if (awayTeamActions.length() == 0) {
            log.info("no scorer for {} vs {} in {} at {} mins", awayTeamActions, homeTeam, competiton, mins);
        } else {
            // if we got this far, away team is the first scorer
            String awayTeamScorer = getScorerName(awayTeamActions);
            Integer awayTeamScorerTime = getScorerTime(awayTeamActions);

            log.info("{} scored for {} at {} mins vs {} in {}", awayTeamScorer, awayTeamName, awayTeamScorerTime, homeTeamName, competiton);
            return Player.builder().name(awayTeamScorer).build();
        }

        //no scorers yet. Is it finished?
        if (result.getObject().getJSONObject("event").getJSONObject("eventProgress").getString("status").equals("RESULT")) {
            log.info("{}  vs {} in {} is finished 0-0", homeTeamName, awayTeamName, competiton);
            return Player.builder().name("0-0").build();
        }

        return null;
    }

    private String getScorerName (JSONArray teamActions) {
        return teamActions.getJSONObject(0).getJSONObject("name").getString("last");
    }

    private Integer getScorerTime (JSONArray teamActions) {
        return teamActions
            .getJSONObject(0).getJSONArray("actions").getJSONObject(0).getInt("timeElapsed");
    }
}
