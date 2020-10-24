package com.firstgoal.controller;

import com.firstgoal.controller.request.AddParticpantRequest;
import com.firstgoal.controller.request.CreateCompetitonRequest;
import com.firstgoal.controller.request.UpdateCompetitionRequest;
import com.firstgoal.service.CompetitionService;
import com.firstgoal.web.Competition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Competition createCompetiton(@RequestBody CreateCompetitonRequest request) {
        return competitionService.createCompetition(request.getLineupUrl(), request.getEventUrl(), request.getOwner());
    }

    @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public Competition updateCompetition(@PathVariable String id, @RequestBody UpdateCompetitionRequest updateCompetitionRequest) {
        return competitionService.addSelectionsToCompetition(id, updateCompetitionRequest.getSelections());
    }

    @GetMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public Competition getCompetiton(@PathVariable String id) {
        return competitionService.getCompetition(id);
    }
    @DeleteMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompetition(@PathVariable String id) {
        competitionService.deleteCompetition(id);
    }

    @PostMapping(path = "/{id}/participants", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Competition addParticpantToCompetition(@PathVariable String id, @RequestBody AddParticpantRequest addParticpantRequest) {
        return competitionService.addParticipant(id, addParticpantRequest.getName());
    }

    @PostMapping(path = "/{id}/start", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Competition start(@PathVariable String id) {
        return competitionService.startCompetition(id);
    }

    @DeleteMapping(path = "/{id}/participants/{name}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(@PathVariable String id, @PathVariable String name) {
        competitionService.removeParticipants(id, new String[]{name});
    }

}
