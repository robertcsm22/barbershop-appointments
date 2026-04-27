package com.barbershop.repository;

import com.barbershop.model.Appointment;
import com.barbershop.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClienteEmail(String clienteEmail);

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.estado = 'RESERVADA'
          AND a.fechaHora < :fin
          AND :inicio < a.fechaHora
          AND (:excludeId IS NULL OR a.id <> :excludeId)
        """)
    List<Appointment> findOverlapping(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("excludeId") Long excludeId
    );

    Optional<Appointment> findByIdAndEstado(Long id, EstadoCita estado);
}