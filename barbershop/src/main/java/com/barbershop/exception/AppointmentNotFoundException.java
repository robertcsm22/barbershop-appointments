package com.barbershop.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("No se encontró una cita activa con id: " + id);
    }
}
