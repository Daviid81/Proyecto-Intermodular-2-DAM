package es.dam.controlparental.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class ValidadorIdentificadores {

    private ValidadorIdentificadores() {}

    public static void validarIdTutor(long idTutor) {

        if (idTutor <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del tutor debe ser positivo"
            );
        }
    }

    public static void validarIdMenor(long idMenor) {

        if (idMenor <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del menor debe ser positivo"
            );
        }
    }
}
