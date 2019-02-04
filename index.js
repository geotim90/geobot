const config = require('./config.json')

const Discord = require('discord.js')
const Enmap = require('enmap')

const { trackHealth } = require('./health')
const { trackActivity } = require('./tracker')

const { installPing } = require('./ping')
const { installInspect } = require('./inspect')

// initialize Discord client
const client = new Discord.Client()

// initialize Enmap database
client.db = new Enmap({
    name: 'geobot',
    fetchAll: false,
    autoFetch: true,
    cloneLevel: 'deep'
})

// track bot health
trackHealth(client)

// track user activity
trackActivity(client)

// install commands
const handlers = {}
installPing(handlers)
installInspect(handlers)

// message handler
client.on('message', onMessage)
function onMessage(message) {
    if (isRelevantMessage(message)) {
        // extract args from message
        message.args = message.content.split(/\s+/g)
        if (message.args[1]) {
            for (const prefix in handlers) {
                if (message.args[1] === prefix) {
                    handlers[prefix](message)
                    break
                }
            }
        }
    }
}

function isRelevantMessage(message) {
    return (
        // message must be posted in a guild
        message.guild
        // ignore all bots
        && !message.author.bot
        // message must contain a parsable string
        && typeof message.content === 'string'
        // this bot must be addressed directly
        && message.content.startsWith('<@' + message.client.user.id + '>')
    )
}

// start client
client.login(config.token)
