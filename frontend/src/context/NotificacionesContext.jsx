import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { useAuth } from './AuthContext'
import api from '../api/axios'

const INTERVALO_MS = 20000

const NotificacionesContext = createContext(null)

/**
 * Una sola consulta periódica de notificaciones no leídas para toda la app:
 * de aquí salen el contador del Navbar y los avisos emergentes (toasts).
 */
export function NotificacionesProvider({ children }) {
    const { usuario } = useAuth()
    const [noLeidas, setNoLeidas] = useState([])
    const [cargadas, setCargadas] = useState(false) // ya ha llegado la primera respuesta

    const refrescar = useCallback(async () => {
        if (!usuario) return
        try {
            const res = await api.get(`/notificaciones/no-leidas?size=20`)
            setNoLeidas(res.data.content || res.data || [])
            setCargadas(true)
        } catch (err) {
            console.error('Error cargando notificaciones:', err)
        }
    }, [usuario])

    useEffect(() => {
        if (!usuario) {
            setNoLeidas([])
            setCargadas(false)
            return
        }
        refrescar()
        // Con la pestaña en segundo plano no se pregunta (así Neon puede suspender la base de datos); al volver, se refresca
        const intervalo = setInterval(() => { if (!document.hidden) refrescar() }, INTERVALO_MS)
        const alVolver = () => { if (!document.hidden) refrescar() }
        document.addEventListener('visibilitychange', alVolver)
        return () => { clearInterval(intervalo); document.removeEventListener('visibilitychange', alVolver) }
    }, [usuario, refrescar])

    return (
        <NotificacionesContext.Provider value={{ noLeidas, cargadas, numNoLeidas: noLeidas.length, refrescar }}>
            {children}
        </NotificacionesContext.Provider>
    )
}

// El hook vive junto a su Provider (patrón habitual de Context)
// eslint-disable-next-line react-refresh/only-export-components
export const useNotificaciones = () => useContext(NotificacionesContext) || { noLeidas: [], cargadas: false, numNoLeidas: 0, refrescar: () => {} }
