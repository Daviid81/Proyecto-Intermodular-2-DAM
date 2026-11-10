package es.dam.controlparental.controller;

import es.dam.controlparental.service.EstadoBackendService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadoBackendController {
    private final EstadoBackendService servicio;

    public EstadoBackendController(EstadoBackendService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/api/estado")
    public ResponseEntity<Map<String, String>> estado() {
        if (servicio.postgresDisponible()) {
            return ResponseEntity.ok(Map.of("postgres", "conectado"));
        }
        return ResponseEntity.status(503).body(Map.of("postgres", "no disponible"));
    }
}
