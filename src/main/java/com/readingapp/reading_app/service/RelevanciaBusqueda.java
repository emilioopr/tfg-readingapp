package com.readingapp.reading_app.service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RelevanciaBusqueda {

    private RelevanciaBusqueda() {}

    public record Candidato(String titulo, String autor, Integer numValoraciones,
                            boolean tienePortada, boolean tieneSinopsis, String idioma) {}

    public static double puntuar(String consulta, Candidato c) {
        return puntuar(consulta, c, false);
    }

    public static double puntuar(String consulta, Candidato c, boolean busquedaDeAutor) {
        String q = normalizar(consulta);
        String titulo = normalizar(c.titulo());
        String tituloPrincipal = normalizar(c.titulo().split("[:.(]")[0]);
        String autor = normalizar(c.autor());
        if (q.isEmpty()) return 0;

        double puntos = 0;

        double porTitulo = 0;
        if (tituloPrincipal.equals(q) || titulo.equals(q)) porTitulo = 100;
        else if (titulo.startsWith(q + " ") || tituloPrincipal.startsWith(q)) porTitulo = 60;
        else if (contieneFrase(titulo, q)) porTitulo = 40;
        else if (contieneTodas(titulo, q)) porTitulo = 20;

        boolean coincideAutor = esNombreDePersona(autor) && (contieneFrase(autor, q) || contieneTodas(autor, q));
        if (busquedaDeAutor && !coincideAutor) porTitulo *= 0.4;
        puntos += porTitulo;
        if (coincideAutor) puntos += 70;
        else if (porTitulo == 0 && contieneTodas(titulo + " " + autor, q)) puntos += 30;

        int valoraciones = c.numValoraciones() != null ? c.numValoraciones() : 0;
        puntos += Math.min(20, 5 * (Math.log(1 + valoraciones) / Math.log(2)));
        if (c.tienePortada()) puntos += 5;
        if (c.tieneSinopsis()) puntos += 3;
        if ("es".equals(c.idioma())) puntos += 5;

        if (c.titulo().length() > 120) puntos -= 10;

        return puntos;
    }

    public static boolean esBusquedaDeAutor(String consulta, List<String> autores) {
        String q = normalizar(consulta);
        long coincidencias = autores.stream().map(RelevanciaBusqueda::normalizar)
                .filter(a -> contieneFrase(a, q) || contieneTodas(a, q)).count();
        return coincidencias >= 3;
    }

    public static boolean mismaObra(String titulo1, String autor1, String titulo2, String autor2) {
        if (!tituloPrincipal(titulo1).equals(tituloPrincipal(titulo2))) return false;
        Set<String> palabras1 = palabrasDelNombre(autor1);
        return palabrasDelNombre(autor2).stream().anyMatch(palabras1::contains);
    }

    private static String tituloPrincipal(String titulo) {
        return normalizar(titulo.split("[:.(/]")[0]);
    }

    private static Set<String> palabrasDelNombre(String autor) {
        return Arrays.stream(normalizar(autor).split(" "))
                .filter(p -> p.length() >= 4)
                .collect(Collectors.toSet());
    }

    static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9ñ ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static boolean esNombreDePersona(String autorNormalizado) {
        return autorNormalizado.split(" ").length <= 5;
    }

    private static boolean contieneFrase(String texto, String frase) {
        return (" " + texto + " ").contains(" " + frase + " ");
    }

    private static boolean contieneTodas(String texto, String consulta) {
        String conEspacios = " " + texto + " ";
        return Arrays.stream(consulta.split(" ")).allMatch(p -> conEspacios.contains(" " + p + " "));
    }
}
