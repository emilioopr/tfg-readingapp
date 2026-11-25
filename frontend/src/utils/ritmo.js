export const FRASES_RITMO = ['', 'Se lee con calma', 'Se lee con soltura', 'Se lee de un tirón']

export const nivelRitmo = (r) => Math.min(3, Math.max(1, Math.round(r)))
