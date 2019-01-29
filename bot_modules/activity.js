const { checkAuthorization } = require('../bot_utils/auth')
const { findGame } = require('../bot_utils/discord-utils')
const { abbreviate, formatTimestamp } = require('../bot_utils/format')
const { reply } = require('../bot_utils/messaging')

function install(registerMessageHandler, registerPresenceUpdateHandler) {
    console.log('[TRACE] activity.install(registerMessageHandler, registerPresenceUpdateHandler)')
    registerMessageHandler('a', handleMessage)
    registerMessageHandler('activity', handleMessage)
    registerPresenceUpdateHandler(handlePresenceUpdate)
}

function handleMessage(msg) {
    console.log(`[TRACE] activity.handleMessage(Message@${msg.id})`)
    if (checkAuthorization(msg, 'activity')) {
        if (msg.args[2] && msg.args[2].match(/^(track|add|\+)$/i)) {
            trackGame(msg, msg.args[3])
        } else if (msg.args[2] && msg.args[2].match(/^(untrack|remove|delete|\-)$/i)) {
            untrackGame(msg, msg.args[3])
        } else if (msg.args[2] && msg.args[2].match(/^g(ame)?$/i)) {
            listGameActivity(msg, msg.args[3])
        } else if (msg.args[2] && msg.args[2].match(/^u(ser)?$/i)) {
            listUserActivity(msg, msg.args[3])
        } else {
            reply(msg, 'here are some examples on how to use `activity`:```java\n'
                + '// start tracking game activity\n@Geobot activity track 422772752647323649\n@Geobot activity track Warframe\n\n'
                + '// stop tracking game activity (deletes data!)\n@Geobot activity untrack 422772752647323649\n@Geobot activity untrack Warframe\n\n'
                + '// list tracked game activity (all users)\n@Geobot activity game 422772752647323649\n@Geobot activity game Warframe\n\n'
                + `// list tracked user activity (all games)\n@Geobot activity user ${msg.author.id}\n@Geobot activity user ${msg.author.username}\n\n`
                + '```')
        }
    }
}

function trackGame(msg, input) {
    console.log(`[TRACE] activity.trackGame(Message@${msg.id}, ${JSON.stringify(input)})`)
    findGame(msg, input).then(game => {
        msg.client.db.set(msg.guild.id, game.name, `games.${game.applicationID}`)
        reply(msg, `acknowledged. I will start tracking the activity of all guild members for ${JSON.stringify(game.name)} (applicationID: ${game.applicationID}).`)
    }).catch(noGame => {
        console.log(`[DEBUG] Could not find game ${JSON.stringify(input)} - reason: ${JSON.stringify(noGame.message)}`)
        reply(msg, `no game ${JSON.stringify(input)} found`)
    })
}

function untrackGame(msg, input) {
    console.log(`[TRACE] activity.untrackGame(Message@${msg.id}, ${JSON.stringify(input)})`)
    findGame(msg, input).then(game => {
        msg.client.db.delete(msg.guild.id, 'games.' + game.applicationID)
        reply(msg, `acknowledged. I will no longer track the activity of any guild members for ${JSON.stringify(game.name)} (applicationID: ${game.applicationID}) and have deleted any related previous tracking data.`)
    }).catch(noGame => {
        console.log(`[DEBUG] Could not find game ${JSON.stringify(input)} - reason: ${JSON.stringify(noGame.message)}`)
        reply(msg, `no game ${JSON.stringify(input)} found`)
    })
}

function listGameActivity(msg, input) {
    console.log(`[TRACE] activity.listGameActivity(Message@${msg.id}, ${JSON.stringify(input)})`)
    findGame(msg, input).then(game => {
        const db = msg.client.db
        if (db.has(msg.guild.id) && db.has(msg.guild.id, `games.${game.applicationID}`)) {
            const activity = db.get(msg.guild.id, `activity.${game.applicationID}`)
            const response = `here is when I saw users play ${JSON.stringify(game.name)} (applicationID: ${game.applicationID}):`
            reply(msg, response + '```java\n' + abbreviate(formatGameActivity(msg, activity), 2000 - response.length - 34) + '```')
        } else {
            reply(msg, `no activity for game ${JSON.stringify(game.name)} (applicationID: ${game.applicationID}) found`)
        }
    }).catch(noGame => {
        console.log(`[DEBUG] Could not find game ${JSON.stringify(input)} - reason: ${JSON.stringify(noGame.message)}`)
        reply(msg, `no game ${JSON.stringify(input)} found`)
    })
}

function formatGameActivity(msg, activity) {
    let output = ''
    for (userId in activity) {
        const member = msg.guild.members.get(userId)
        if (member) {
            output += `@${member.nickname ? member.nickname : member.user.username} - ${formatTimestamp(activity[userId])}\n`
        }
    }
    return output
}

function listUserActivity(msg, input) {
    console.log(`[TRACE] activity.listUserActivity(Message@${msg.id}, ${JSON.stringify(input)})`)
    // TODO
}

function handlePresenceUpdate(oldMember, newMember) {
    console.log(`[TRACE] activity.handlePresenceUpdate(GuildMember@${oldMember.id}, GuildMember@${newMember.id})`)
    logGameActivity(oldMember)
    logGameActivity(newMember)
}

function logGameActivity(member) {
    console.log(`[TRACE] activity.logGameActivity(GuildMember@${member.id})`)
    const db = member.client.db
    const game = member.presence.game
    if (game && game.applicationID && game.timestamps
        && db.has(member.guild.id) && db.has(member.guild.id, `games.${game.applicationID}`)
    ) {
        if (game.timestamps.end) {
            console.log(`[INFO] Updating activity for Guild@${member.guild.id} Game@${game.applicationID} GuildMember@${member.id} Date(${game.timestamps.end.getTime()})`)
            member.client.db.set(member.guild.id, game.timestamps.end.getTime(), `activity.${game.applicationID}.${member.id}`)
        } else if (game.timestamps.start) {
            console.log(`[INFO] Updating activity for Guild@${member.guild.id} Game@${game.applicationID} GuildMember@${member.id} Date(${game.timestamps.start.getTime()})`)
            member.client.db.set(member.guild.id, game.timestamps.start.getTime(), `activity.${game.applicationID}.${member.id}`)
        }
    }
}

module.exports = { install }
