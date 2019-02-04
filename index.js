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

// track bot health
client.on('debug', info => console.log(`[HEALTH] [DEBUG] ${info}`))
client.on('disconnect', event => console.log(`[HEALTH] DISCONNECT: ${JSON.stringify(event)}`))
client.on('error', event => console.log(`[HEALTH] [ERROR] ${JSON.stringify(event)}`))
client.on('rateLimit', rateLimitInfo => console.log(`[HEALTH] RATE LIMIT: ${JSON.stringify(rateLimitInfo)}`))
client.on('ready', () => {
    console.log(`[HEALTH] READY`)
    setWarframePresence()
})
client.on('reconnecting', () => console.log(`[HEALTH] RECONNECTING`))
client.on('resume', replayed => console.log(`[HEALTH] RESUME after ${replayed}`))
client.on('warn', info => console.log(`[HEALTH] [WARN] ${info}`))

function setWarframePresence() {
    client.user.setPresence({
        status: 'online',
        afk: false,
        game: {
            name: 'Warframe',
            type: 0,
            details: 'Lobby',
            state: null,
            applicationID: '422772752647323649',
            timestamps: { start: null, end: null },
            party: {},
            assets: {
                largeText: null,
                smallText: null,
                largeImage: '422773248191627264',
                smallImage: null
            }
        }
    })
        .then(presence => console.log(`[ACTION] Activity set`, config.debug ? presence : ''))
        .catch(console.error)
}

// track guild member activity
client.on('message', onMessage)
client.on('presenceUpdate', onPresenceUpdate)

function onMessage(message) {
    if (isMemberMessage(message)) {
        console.log(`[EVENT] {guild=${message.guild.id}|message=${message.id}} Message`, config.debug ? message : '')
        // update "lastMessage"
        updateLastMessage(message)
    }
}

function onPresenceUpdate(oldMember, newMember) {
    if (isMemberPresenceUpdate(newMember)) {
        console.log(`[EVENT] {guild=${newMember.guild.id}|member=${newMember.id}} Presence update`, config.debug ? newMember.presence : '')
        // update "lastOnline"
        if (isMemberOnline(oldMember)) {
            updateLastOnline(oldMember)
        } else if (isMemberOnline(newMember)) {
            updateLastOnline(newMember)
        }
        // update game activity
        if (isMemberPlayingGame(oldMember)) {
            updateLastPlaying(oldMember)
        } else if (isMemberPlayingGame(newMember)) {
            updateLastPlaying(newMember)
        }
    }
}

function isMemberMessage(message) {
    return (
        // message must be posted in a guild
        message.guild
        // ignore all bots
        && !message.author.bot
        // message must contain a parsable string
        && typeof message.content === 'string'
    )
}

function isMemberPresenceUpdate(member) {
    return (
        // member must be in a guild
        member.guild
        // ignore all bots
        && !member.user.bot
    )
}

function isMemberOnline(member) {
    return (
        member.presence
        && member.presence.status !== 'offline'
    )
}

function isMemberPlayingGame(member) {
    return (
        member.presence
        && member.presence.game
        && member.presence.game.type === 0
        && member.presence.game.applicationID
    )
}

function updateLastMessage(message) {
    console.log(`[UPDATE] {guild=${message.guild.id}|member=${message.author.id}} lastMessage`, message.createdTimestamp)
    message.client.db.set(message.guild.id, message.createdTimestamp, `${message.author.id}.lastMessage`)
}

function updateLastOnline(member) {
    const now = new Date().getTime()
    console.log(`[UPDATE] {guild=${member.guild.id}|member=${member.id}} lastOnline`, now)
    member.client.db.set(member.guild.id, now, `${member.id}.lastOnline`)
}

function updateLastPlaying(member) {
    const now = new Date().getTime()
    console.log(`[UPDATE] {guild=${member.guild.id}|member=${member.id}|game=${member.presence.game.applicationID}} lastPlaying`, now)
    member.client.db.set(member.guild.id, now, `${member.id}.${member.presence.game.applicationID}`)
}

// track removals for database clean-up
client.on('guildDelete', onGuildDelete)
client.on('guildMemberRemove', onGuildMemberRemove)
client.on('roleDelete', onRoleDelete)

function onGuildDelete(guild) {

}

function onGuildMemberRemove(member) {

}

function onRoleDelete(role) {

}

// start client
client.login(config.token)
