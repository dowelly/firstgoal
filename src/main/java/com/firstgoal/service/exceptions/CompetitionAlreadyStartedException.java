package com.firstgoal.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Competition already started")
public class CompetitionAlreadyStartedException extends RuntimeException {
    public CompetitionAlreadyStartedException() {super("Competition already started");}
}
