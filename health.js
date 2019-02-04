function trackHealth(client) {
    client.on('debug', info => console.log(`[HEALTH] [DEBUG] ${info}`))
    client.on('disconnect', event => console.log(`[HEALTH] DISCONNECT: ${JSON.stringify(event)}`))
    client.on('error', event => console.log(`[HEALTH] [ERROR] ${JSON.stringify(event)}`))
    client.on('rateLimit', rateLimitInfo => console.log(`[HEALTH] RATE LIMIT: ${JSON.stringify(rateLimitInfo)}`))
    client.on('ready', () => console.log(`[HEALTH] READY`))
    client.on('reconnecting', () => console.log(`[HEALTH] RECONNECTING`))
    client.on('resume', replayed => console.log(`[HEALTH] RESUME after ${replayed}`))
    client.on('warn', info => console.log(`[HEALTH] [WARN] ${info}`))
}

module.exports = { trackHealth }
