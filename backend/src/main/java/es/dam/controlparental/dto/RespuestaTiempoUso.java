package es.dam.controlparental.dto;

public record RespuestaTiempoUso(
        long idMenor,
        int tiempoSemana,
        int tiempoFinde
) {}