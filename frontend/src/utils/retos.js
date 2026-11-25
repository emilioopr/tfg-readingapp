export const formatoProgreso = (tipo, progreso = 0, meta = 0) =>
    tipo === 'HORAS' ? `${(progreso / 60).toFixed(1)} / ${meta} h` : `${progreso} / ${meta}`
