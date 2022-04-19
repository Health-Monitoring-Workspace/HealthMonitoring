package com.example.healthmonitoring.internal.errors;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

/**
 * Controller for errors.
 */
@ControllerAdvice
@Log4j2
public class ErrorController {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> exception(final Throwable throwable, final Model model) {
        return Mono.just(throwable)
                .map(throwable1 -> {
                    log.error("Exception during execution of SpringSecurity application", throwable);
                    final String errorMessage = throwable.getMessage();
                    model.addAttribute("errorMessage", errorMessage);
                    return throwable1;
                })
                .thenReturn("general/error");
    }
}
