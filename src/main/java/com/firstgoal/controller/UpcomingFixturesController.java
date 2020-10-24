package com.firstgoal.controller;

import com.firstgoal.service.UpcomingFixturesService;
import com.firstgoal.web.UpcomingFixture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/upcomingFixtures")
public class UpcomingFixturesController {

    @Autowired
    private UpcomingFixturesService upcomingFixturesService;

    @GetMapping(produces = "application/json")
    public List<UpcomingFixture> getUpcomingFixtures() {
        return upcomingFixturesService.getUpcomingFixtures();
    }
}
