import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { useAuth } from './AuthContext'
import api from '../api/axios'

const INTERVALO_MS = 20000

const NotificacionesContext = createContext(null)

export function NotificacionesProvider({ children }) {
    const { usuario } = useAuth()
    const [noLeidas, setNoLeidas] = useState([])
    const [cargadas, setCargadas] = useState(false)

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

// eslint-disable-next-line react-refresh/only-export-components
export const useNotificaciones = () => useContext(NotificacionesContext) || { noLeidas: [], cargadas: false, numNoLeidas: 0, refrescar: () => {} }
