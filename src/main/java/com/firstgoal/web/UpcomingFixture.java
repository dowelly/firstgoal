package com.firstgoal.web;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpcomingFixture {
    private String homeTeam;
    private String awayTeam;
    private String lineUpUrl;
    private String eventUrl;
}
