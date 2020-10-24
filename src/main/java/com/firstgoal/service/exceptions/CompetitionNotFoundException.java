package com.firstgoal.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Competition not found")
public class CompetitionNotFoundException extends RuntimeException {
    public CompetitionNotFoundException() {super("Competition not found");}
}
