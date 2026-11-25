package com.readingapp.reading_app.dto;

import lombok.*;

import java.util.List;

public class CatalogoDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LibroResumen {
        private Long idlibro;
        private String titulo;
        private String portada;
        private String nombreAutor;
        private Integer anioPublicacion;
        private String categoria;
        private Double notaMedia;
        private Integer numResenas;
        private String sinopsis;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Fila {
        private String clave;
        private String titulo;
        private String slugCategoria;
        private List<LibroResumen> libros;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CategoriaInfo {
        private String slug;
        private String nombre;
        private Integer total;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaginaCategoria {
        private String slug;
        private String nombre;
        private Integer total;
        private Integer pagina;
        private Boolean hayMas;
        private List<LibroResumen> libros;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private LibroResumen destacado;
        private List<Fila> filas;
        private List<CategoriaInfo> categorias;
    }
}
