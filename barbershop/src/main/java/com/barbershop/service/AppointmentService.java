package com.barbershop.service;

import com.barbershop.dto.AppointmentRequest;
import com.barbershop.exception.AppointmentConflictException;
import com.barbershop.exception.AppointmentNotFoundException;
import com.barbershop.model.Appointment;
import com.barbershop.model.EstadoCita;
import com.barbershop.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentService {

    private static final int DEFAULT_DURATION_MIN = 30;

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all appointments, optionally filtered by email.
     */
   @Transactional(readOnly = true)   // ✅ Solo UNA vez, con la anotación
    public List<Appointment> findAll(String clienteEmail) {
        if (clienteEmail != null && !clienteEmail.isBlank()) {
            return repository.findByClienteEmail(clienteEmail);
        }
        return repository.findAll();
    }

    /**
     * Creates a new appointment.
     * - Applies default duration if not provided.
     * - Checks for overlap with existing RESERVADA appointments.
     * - Wrapped in @Transactional to minimize race conditions.
     */
    @Transactional
    public Appointment create(AppointmentRequest request) {
        int duracion = (request.getDuracionMin() != null) ? request.getDuracionMin() : DEFAULT_DURATION_MIN;
        LocalDateTime inicio = request.getFechaHora();
        LocalDateTime fin = inicio.plusMinutes(duracion);

        // Check for overlapping appointments
        List<Appointment> overlapping = repository.findOverlapping(inicio, fin, null);
        if (!overlapping.isEmpty()) {
            Appointment conflicting = overlapping.get(0);
            String conflictMsg = String.format(
                    "Existe solapamiento con la cita de '%s' programada el %s (duración %d min). " +
                    "El barbero no está disponible en ese horario.",
                    conflicting.getClienteNombre(),
                    conflicting.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    conflicting.getDuracionMin()
            );
            throw new AppointmentConflictException(conflictMsg);
        }

        // Build and save appointment
        Appointment appointment = new Appointment();
        appointment.setClienteNombre(request.getClienteNombre());
        appointment.setClienteEmail(request.getClienteEmail());
        appointment.setClienteTelefono(request.getClienteTelefono());
        appointment.setFechaHora(inicio);
        appointment.setDuracionMin(duracion);
        appointment.setEstado(EstadoCita.RESERVADA);
        appointment.setEspecialidades(request.getEspecialidades());

        return repository.save(appointment);
    }

    /**
     * Cancels an appointment by setting its state to CANCELADA.
     * Returns 404 if the appointment doesn't exist or is already cancelled.
     */
    @Transactional
    public Appointment cancel(Long id) {
        Appointment appointment = repository.findByIdAndEstado(id, EstadoCita.RESERVADA)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        appointment.setEstado(EstadoCita.CANCELADA);
        return repository.save(appointment);
    }


}
