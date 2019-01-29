const { checkAuthorization } = require('../bot_utils/auth')
const { react } = require('../bot_utils/messaging')

const handlers = {}

function registerMessageHandler(prefix, handler) {
    console.log(`[TRACE] messageProcessor.registerMessageHandler(${JSON.stringify(prefix)}, handler)`)
    handlers[prefix.toLowerCase()] = handler
}

function onMessage(msg) {
    // only process relevant messages
    if (isRelevantMessage(msg)) {
        console.log(`[TRACE] messageProcessor.onMessage(Message@${msg.id})`)
        // extract args from message
        msg.args = msg.content.split(/\s+/g)
        console.log(`[DEBUG] Message@${msg.id} has args ${JSON.stringify(msg.args)}`)
        // find first matching handler and call it
        let handlerFound = false
        if (msg.args[1]) {
            for (const prefix in handlers) {
                if (msg.args[1] === prefix) {
                    console.log(`[INFO] Found handler for Message@${msg.id} with prefix ${JSON.stringify(prefix)}`)
                    handlerFound = true
                    handlers[prefix](msg)
                    break
                }
            }
        }
        if (!handlerFound) {
            console.log(`[INFO] No handler found for Message@${msg.id} with args ${JSON.stringify(msg.args)}`)
            if (checkAuthorization(msg, "ping", false)) {
                react(msg, "❓")
            }
        }
    }
}

function isRelevantMessage(msg) {
    return (
        // message must be posted in a guild
        msg.guild
        // ignore all bots
        && !msg.author.bot
        // message must contain a parsable string
        && typeof msg.content === 'string'
        // this bot must be addressed directly
        && msg.content.startsWith('<@' + msg.client.user.id + '>')
    )
}

module.exports = { registerMessageHandler, onMessage }
