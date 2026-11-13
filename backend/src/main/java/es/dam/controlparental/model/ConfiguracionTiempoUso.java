package es.dam.controlparental.model;

public record ConfiguracionTiempoUso(
        long idMenor,
        int tiempoSemana,
        int tiempoFinde
) {}
