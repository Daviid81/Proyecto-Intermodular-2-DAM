package es.dam.controlparental.model;

public record Menor(
        long idMenor,
        long idTutor,
        String nombre,
        int edad
) {}