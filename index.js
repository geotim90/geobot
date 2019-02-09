const config = require('./config.json')

const Discord = require('discord.js')
const Enmap = require('enmap')

// initialize Discord client
const client = new Discord.Client()

// initialize Enmap database
client.db = new Enmap({
    name: 'geobot',
    fetchAll: false,
    autoFetch: true,
    cloneLevel: 'deep'
})

// start client
client.login(config.token)
