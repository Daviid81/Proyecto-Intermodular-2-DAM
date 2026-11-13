package es.dam.controlparental.model;

public record ConfiguracionModalidadCuestionario(
        long idMenor,
        int minutosAcierto,
        ModoCuestionario modoCuestionario
) {}
