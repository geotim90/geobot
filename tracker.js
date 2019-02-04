const config = require('./config.json')

function trackActivity(client) {
    // track guild member activity
    client.on('message', onMessage)
    client.on('presenceUpdate', onPresenceUpdate)

    // track removals for database clean-up
    client.on('guildDelete', onGuildDelete)
    client.on('guildMemberRemove', onGuildMemberRemove)
    client.on('roleDelete', onRoleDelete)
}

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

function onGuildDelete(guild) {
    console.log(`[UPDATE] {guild=${guild.id}} guildDelete`)
    guild.client.db.delete(guild.id)
}

function onGuildMemberRemove(member) {
    console.log(`[UPDATE] {guild=${member.guild.id}|member=${member.id}} guildMemberRemove`)
    guild.client.db.delete(guild.id, `${member.id}`)
}

function onRoleDelete(role) {
    console.log(`[UPDATE] {guild=${role.guild.id}|role=${role.id}} roleDelete`)
}

module.exports = { trackActivity }
