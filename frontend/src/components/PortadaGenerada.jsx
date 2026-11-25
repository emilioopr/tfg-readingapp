const COLORES = ['#5E2A36', '#233B57', '#2F4A3A', '#46395A', '#3B3530']

function indiceColor(id, titulo) {
    if (id != null && !Number.isNaN(Number(id))) return Math.abs(Number(id)) % COLORES.length
    let h = 0
    for (const c of titulo || '') h = (h * 31 + c.charCodeAt(0)) | 0
    return Math.abs(h) % COLORES.length
}

export default function PortadaGenerada({ id, titulo, autor, className = '' }) {
    return (
        <div role="img" aria-label={`Portada de ${titulo || 'libro sin título'}`}
             className={`@container relative overflow-hidden rounded-[2px] text-texto ${className}`}
             style={{ backgroundColor: COLORES[indiceColor(id, titulo)] }}>
            <span aria-hidden className="absolute inset-y-0 left-0 w-[6%] bg-fondo/25" />
            <div className="absolute inset-0 flex flex-col justify-between p-[8cqw]">
                <p className="font-serif leading-[1.1] text-[12cqw] line-clamp-5 pl-[4%] break-words @max-[3.5rem]:hidden">
                    {titulo}
                </p>
                {autor && (
                    <p className="font-sans uppercase tracking-[0.12em] text-[6.5cqw] text-texto/80 line-clamp-2 pl-[4%] @max-[3.5rem]:hidden">
                        {autor}
                    </p>
                )}
            </div>
        </div>
    )
}
