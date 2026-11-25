export function mejorarPortada(url) {
    if (!url) return url
    return url
        .replace(/^http:\/\//i, 'https://')
        .replace(/[?&]edge=curl/gi, (m) => (m.startsWith('?') ? '?' : ''))
        .replace(/\?&/, '?')
}
