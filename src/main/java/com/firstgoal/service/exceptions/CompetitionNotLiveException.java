package com.firstgoal.service.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Competition not yet live")
public class CompetitionNotLiveException extends RuntimeException{
    public CompetitionNotLiveException() {super("Competition not yet live");}
}
