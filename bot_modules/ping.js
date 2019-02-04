function installPing(handlers) {
    handlers['ping'] = onPing
}

function onPing(message) {
    message.reply("Pong!")
}

module.exports = { installPing }
