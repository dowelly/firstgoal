package com.firstgoal.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "Invalid participant numbers")
public class InvalidParticipantNumbersException extends RuntimeException{

    public InvalidParticipantNumbersException() {super("Invalid participant numbers");}
}
