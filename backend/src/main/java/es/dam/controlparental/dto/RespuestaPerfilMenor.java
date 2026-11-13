package es.dam.controlparental.dto;

public record RespuestaPerfilMenor(
        long idMenor,
        long idTutor,
        String nombre,
        int edad
) {}