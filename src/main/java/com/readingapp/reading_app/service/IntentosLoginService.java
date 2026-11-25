package com.readingapp.reading_app.service;

import com.readingapp.reading_app.config.DemasiadosIntentosException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class IntentosLoginService {

    public static final int MAX_FALLOS = 5;
    public static final int MAX_FALLOS_IP = 20;
    public static final Duration DURACION_BLOQUEO = Duration.ofMinutes(15);

    private final Clock clock;
    private final Map<String, Estado> intentos = new ConcurrentHashMap<>();

    private record Estado(int fallos, Instant bloqueadoHasta) {}

    public void comprobar(String email) {
        Estado estado = intentos.get(email);
        if (estado == null || estado.bloqueadoHasta() == null) return;

        Instant ahora = Instant.now(clock);
        if (ahora.isBefore(estado.bloqueadoHasta())) {
            long segundos = Duration.between(ahora, estado.bloqueadoHasta()).toSeconds();
            long minutos = Math.max(1, (segundos + 59) / 60);
            throw new DemasiadosIntentosException("Demasiados intentos fallidos. Inténtalo de nuevo en "
                    + minutos + (minutos == 1 ? " minuto" : " minutos"));
        }
        intentos.remove(email);
    }

    public void registrarFallo(String email) {
        registrarFallo(email, MAX_FALLOS);
    }

    public void comprobarIp(String ip) {
        comprobar("ip:" + ip);
    }

    public void registrarFalloIp(String ip) {
        registrarFallo("ip:" + ip, MAX_FALLOS_IP);
    }

    private void registrarFallo(String clave, int maxFallos) {
        intentos.compute(clave, (e, estado) -> {
            int fallos = (estado == null ? 0 : estado.fallos()) + 1;
            Instant bloqueo = fallos >= maxFallos ? Instant.now(clock).plus(DURACION_BLOQUEO) : null;
            return new Estado(fallos, bloqueo);
        });
    }

    public void registrarExito(String email) {
        intentos.remove(email);
    }
}
