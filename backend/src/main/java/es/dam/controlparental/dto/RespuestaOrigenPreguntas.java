package es.dam.controlparental.dto;

import es.dam.controlparental.model.OrigenPreguntas;

public record RespuestaOrigenPreguntas(
        long idMenor,
        OrigenPreguntas origen
) {}