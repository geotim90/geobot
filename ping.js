const { checkAuthorization } = require('./auth')

function installPing(handlers) {
    handlers['ping'] = onPing
}

function onPing(message) {
    console.log(`[TRACE] ping.onPing(Message@${message.id})`)
    if (checkAuthorization(message, 'ping')) {
        message.reply('Pong!')
    }
}

module.exports = { installPing }
