export const ETIQUETAS = [
    'MELANCOLICO', 'TRISTE', 'ALEGRE', 'OSCURO', 'ESPERANZADOR',
    'TENSO', 'ROMANTICO', 'DIVERTIDO', 'REFLEXIVO', 'INQUIETANTE',
    'INSPIRADOR', 'NOSTALGICO', 'MISTERIOSO', 'EPICO', 'INTIMO',
    'PERTURBADOR', 'RECONFORTANTE', 'AVENTURERO', 'POETICO', 'PROVOCADOR',
]

const CON_TILDE = { MELANCOLICO: 'melancólico', ROMANTICO: 'romántico', NOSTALGICO: 'nostálgico', EPICO: 'épico', INTIMO: 'íntimo', POETICO: 'poético' }

export const nombreEtiqueta = (valor) => CON_TILDE[valor?.toUpperCase()] || valor?.toLowerCase()
