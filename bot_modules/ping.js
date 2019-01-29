const { checkAuthorization } = require('../bot_utils/auth')
const { reply } = require('../bot_utils/messaging')

function install(registerMessageHandler) {
    console.log('[TRACE] ping.install(registerMessageHandler)')
    registerMessageHandler('ping', onPing)
    registerMessageHandler('🏓', onPingPong)
}

function onPing(msg) {
    console.log(`[TRACE] ping.onPing(Message@${msg.id})`)
    if (checkAuthorization(msg, 'ping', false)) {
        reply(msg, 'Pong!')
    }
}

function onPingPong(msg) {
    console.log(`[TRACE] ping.onPingPong(Message@${msg.id})`)
    checkAuthorization(msg, 'ping', '🏓')
}

module.exports = { install }
