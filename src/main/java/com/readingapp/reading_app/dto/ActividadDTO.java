package com.readingapp.reading_app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class ActividadDTO {

    public enum Tipo { EMPEZO, TERMINO, QUIERE_LEER, RESENA, RETO_CONSEGUIDO }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Tipo tipo;
        private String fecha;
        private Long idusuario;
        private String nombreUsuario;
        private String avatarUsuario;
        private Long idlibro;
        private String tituloLibro;
        private String portadaLibro;
        private String nombreAutor;
        private Long idresena;
        private BigDecimal puntuacion;
        private Long idreto;
        private String tituloReto;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Pagina {
        private List<Response> eventos;
        private Integer pagina;
        private Boolean hayMas;
    }
}
