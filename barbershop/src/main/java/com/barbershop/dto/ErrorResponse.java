package com.barbershop.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private int status;
    private String error;
    private String mensaje;
    private List<String> detalles;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String mensaje, List<String> detalles) {
        this(status, error, mensaje);
        this.detalles = detalles;
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMensaje() { return mensaje; }
    public List<String> getDetalles() { return detalles; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
