package com.barbershop.controller;

import com.barbershop.dto.AppointmentRequest;
import com.barbershop.model.Appointment;
import com.barbershop.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    /**
     * GET /api/appointments
     * Lists all appointments; optionally filter by clienteEmail.
     */
    @GetMapping
    public ResponseEntity<List<Appointment>> getAll(
            @RequestParam(required = false) String clienteEmail) {
        List<Appointment> appointments = service.findAll(clienteEmail);
        return ResponseEntity.ok(appointments);
    }

    /**
     * POST /api/appointments
     * Creates a new appointment.
     * Returns 201 Created, 400 Bad Request, or 409 Conflict.
     */
    @PostMapping
    public ResponseEntity<Appointment> create(
            @Valid @RequestBody AppointmentRequest request) {
        Appointment created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * DELETE /api/appointments/{id}
     * Cancels an appointment (sets estado = CANCELADA).
     * Returns 200 OK or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Appointment> cancel(@PathVariable Long id) {
        Appointment cancelled = service.cancel(id);
        return ResponseEntity.ok(cancelled);
    }
}

/**holaaa roberto*/