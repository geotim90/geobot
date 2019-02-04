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
function onMessage(msg) {
    if (isRelevantMessage(msg)) {
        // extract args from message
        msg.args = msg.content.split(/\s+/g)
        if (msg.args[1]) {
            for (const prefix in handlers) {
                if (msg.args[1] === prefix) {
                    handlers[prefix](msg)
                    break
                }
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

// start client
client.login(config.token)
