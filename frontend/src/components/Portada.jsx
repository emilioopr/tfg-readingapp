import { useState } from 'react'
import { mejorarPortada } from '../utils/portadas'
import PortadaGenerada from './PortadaGenerada'

export default function Portada({ src, titulo, autor, id, className = 'w-32', elevar = true }) {
    const [fallo, setFallo] = useState(false)
    const mover = elevar ? 'transition-transform duration-300 ease-[cubic-bezier(0.22,1,0.36,1)] group-hover:-translate-y-1' : ''

    if (!src || fallo) {
        return <PortadaGenerada id={id} titulo={titulo} autor={autor} className={`aspect-[2/3] ${mover} ${className}`} />
    }
    return (
        <div className={`relative aspect-[2/3] overflow-hidden rounded-[2px] bg-superficie ${mover} ${className}`}>
            <img src={mejorarPortada(src)} alt={`Portada de ${titulo || 'libro'}`} loading="lazy"
                 onError={() => setFallo(true)} className="absolute inset-0 w-full h-full object-cover" />
            <span aria-hidden className="absolute inset-y-0 left-0 w-[7%] bg-gradient-to-r from-fondo/45 to-transparent" />
            <span aria-hidden className="absolute inset-0 rounded-[2px] ring-1 ring-inset ring-texto/5" />
        </div>
    )
}
