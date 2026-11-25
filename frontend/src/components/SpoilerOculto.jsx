import { useState } from 'react'
import { Warning } from '@phosphor-icons/react'

export default function SpoilerOculto({ activo, children }) {
    const [revelado, setRevelado] = useState(false)

    if (!activo || revelado) return children

    const revelar = (e) => {
        e.stopPropagation()
        setRevelado(true)
    }

    return (
        <div className="relative mt-2 overflow-hidden">
            <div aria-hidden className="blur-md select-none pointer-events-none min-h-[4.5rem] [&>*]:mt-0">
                {children}
            </div>
            <div className="absolute inset-0 flex flex-col items-start justify-center gap-1.5 bg-fondo/60 px-1">
                <p className="text-texto text-sm flex items-center gap-1.5">
                    <Warning aria-hidden size={16} className="text-acento" />
                    Aquí hay spoilers
                </p>
                <button onClick={revelar} className="text-xs text-acento hover:text-acento-hover underline underline-offset-4 decoration-acento/40">
                    Leer de todos modos
                </button>
            </div>
        </div>
    )
}
