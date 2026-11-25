package com.readingapp.reading_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "valoracion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idusuario", "idlibro"}),
        indexes = @Index(name = "idx_valoracion_libro", columnList = "idlibro"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvaloracion;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal puntuacion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlibro", nullable = false)
    private Libro libro;
}
