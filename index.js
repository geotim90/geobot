const config = require('./config.json')

const Discord = require('discord.js')
const Enmap = require('enmap')

const { registerMessageHandler, onMessage } = require('./bot_processors/messageProcessor')
const { registerPresenceUpdateHandler, onPresenceUpdate } = require('./bot_processors/presenceUpdateProcessor')

// initialize Discord client
const client = new Discord.Client()

// initialize Enmap database
client.db = new Enmap({
    name: 'geobot',
    fetchAll: false,
    autoFetch: true,
    cloneLevel: 'deep'
})

// listen for messages
client.on('message', msg => {
    onMessage(msg);
});

// listen for user presence updates
client.on('presenceUpdate', (oldMember, newMember) => {
    onPresenceUpdate(oldMember, newMember)
})

// listen for client login
client.on('ready', () => {
    console.log(`[INFO] Logged in as ${client.user.tag}`)
});

// install handlers
require('./bot_modules/activity').install(registerMessageHandler, registerPresenceUpdateHandler)
require('./bot_modules/inspect').install(registerMessageHandler)
require('./bot_modules/perm').install(registerMessageHandler)
require('./bot_modules/ping').install(registerMessageHandler)

// start client
client.login(config.token)
