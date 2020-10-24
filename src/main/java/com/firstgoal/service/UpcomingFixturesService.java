package com.firstgoal.service;

import com.firstgoal.web.UpcomingFixture;
import com.mashape.unirest.http.JsonNode;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UpcomingFixturesService {

    @Autowired
    private BBCClient bbcClient;


    @SneakyThrows
    public List<UpcomingFixture> getUpcomingFixtures() {

        List<String> upcomingCompetitionUrls =
            List.of(
                "https://push.api.bbci.co.uk/proxy/data/bbc-morph-sport-football-scores-filter-priority-order-data/tournament/premier-league",
                "https://push.api.bbci.co.uk/proxy/data/bbc-morph-sport-football-scores-filter-priority-order-data/tournament/champions-league"
            );

        List<UpcomingFixture> upcomingFixtures = new ArrayList();

        for (String url: upcomingCompetitionUrls) {
            upcomingFixtures.addAll(parseCompetitionUrl(url));
        }

        return upcomingFixtures;
    }

    @SneakyThrows
    private List<UpcomingFixture> parseCompetitionUrl(String url) {

        List<UpcomingFixture> upcomingFixtures = new ArrayList();
        JsonNode response = bbcClient.get(url);
        JSONObject rounds = (JSONObject) ((JSONArray)response.getObject().get("rounds")).get(0);

        Iterator it = ((JSONArray)rounds.get("events")).iterator();

        while (it.hasNext()) {
            upcomingFixtures.add(parseEventToUpcomingFixture((JSONObject) it.next()));
        }

        return upcomingFixtures;
    }

    private UpcomingFixture parseEventToUpcomingFixture(JSONObject event) {
        return UpcomingFixture.builder()
            .awayTeam((String) ((JSONObject)((JSONObject)event.get("awayTeam")).get("name")).get("full"))
            .homeTeam((String) ((JSONObject)((JSONObject)event.get("homeTeam")).get("name")).get("full"))
            .lineUpUrl(String.format("https://push.api.bbci.co.uk/proxy/data/bbc-morph-sport-football-team-lineups-data/event/%s", event.get("eventKey")))
            .eventUrl(String.format("https://push.api.bbci.co.uk/proxy/data/bbc-morph-sport-football-header-data/event/%s", event.get("eventKey")))
            .build();
    }
}
