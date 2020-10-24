package com.firstgoal.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Participant already exists")
public class ParticipantExistsException extends RuntimeException {

    public ParticipantExistsException() {super("Participant already exists");}
}