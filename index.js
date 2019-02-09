const config = require("./config.json")

const Discord = require("discord.js")
const Enmap = require("enmap")

// initialize Discord client
const client = new Discord.Client()

// initialize Enmap database
const db = new Enmap({
    name: "geobot",
    fetchAll: false,
    autoFetch: true,
    cloneLevel: "deep"
})

// logger
function log(type, context, message, obj) {
    if (context) {
        console.log(`${new Date().toISOString()} [${type.toUpperCase()}] {${context}} ${message}`, obj ? obj : "")
    } else {
        console.log(`${new Date().toISOString()} [${type.toUpperCase()}] ${message}`, obj ? obj : "")
    }
}

// track bot health
client.on("debug", info => log("health", false, "[DEBUG]", info))
client.on("disconnect", event => log("health", false, "DISCONNECT", event))
client.on("error", event => log("health", false, "[ERROR]", event.message))
client.on("rateLimit", rateLimitInfo => log("health", false, "RATE LIMIT", rateLimitInfo))
client.on("ready", () => log("health", false, "READY"))
client.on("reconnecting", () => log("health", false, "RECONNECTING"))
client.on("resume", replayed => log("health", false, "RESUME", replayed))
client.on("warn", info => log("health", false, "[WARN]", info))

// track guild member activity
client.on("message", onMessage)
client.on("presenceUpdate", onPresenceUpdate)

// track removals for database clean-up
client.on("guildDelete", onGuildDelete)
client.on("guildMemberRemove", onGuildMemberRemove)
client.on("roleDelete", onRoleDelete)

// process commands
const PING = /^ping\b/i
const HELP = /^help\b/i
const REPORT = /^report\b/i
const CONTRIBUTION = /^contribution\b/i
const SET = /^set\b/i
const GET = /^get\b/i
const UNSET = /^unset\b/i
const ROLE = /^role\b/i
const ADD = /^add\b/i
const REMOVE = /^remove\b/i
const TIMEOUT = /^timeout\b/i
const MEMBER = /^member\b/i
const GAME = /^game\b/i

function onMessage(message) {
    if (isRelevantMessage(message)) {
        log("event", `guild=${message.guild.id}|message=${message.id}`, "message")
        if (isBotCommand(message) && isAuthorModOrAdmin(message)) {
            const admin = isAuthorAdmin(message)
            // extract cmd and args from message
            const cmd = message.content.substring(message.content.indexOf('>') + 1).trim()
            const args = cmd.split(/\s+/g)
            // process command
            log("command", `guild=${message.guild.id}|message=${message.id}`, JSON.stringify(cmd))
            if (PING.test(cmd)) {
                doPing(message)
            } else if (HELP.test(cmd)) {
                doHelp(message, args[1])
            } else if (REPORT.test(cmd)) {
                doReport(message, args[1])
            } else if (CONTRIBUTION.test(args[1])) {
                if (SET.test(args[0])) {
                    doSetContribution(message, args[2])
                } else if (GET.test(args[0])) {
                    doGetContribution(message, args[2])
                } else if (admin && UNSET.test(args[0])) {
                    doUnsetContribution(message, args[2])
                } else {
                    doHelp(message, args[1])
                }
            } else if (admin) {
                if (ROLE.test(args[1])) {
                    if (ADD.test(args[0])) {
                        doAddRole(message, args[2], args[3])
                    } else if (REMOVE.test(args[0])) {
                        doRemoveRole(message, args[2], args[3])
                    } else if (GET.test(args[0])) {
                        doGetRole(message, args[2], args[3])
                    } else {
                        doHelp(message, args[1])
                    }
                } else if (TIMEOUT.test(args[1])) {
                    if (SET.test(args[0])) {
                        doSetTimeout(message, args[2], args[3])
                    } else if (GET.test(args[0])) {
                        doGetTimeout(message, args[2])
                    } else {
                        doHelp(message, args[1])
                    }
                } else if (MEMBER.test(args[1])) {
                    if (SET.test(args[0])) {
                        doSetMember(message, args[2], args[3], args[4])
                    } else if (GET.test(args[0])) {
                        doGetMember(message, args[2], args[3])
                    } else {
                        doHelp(message, args[1])
                    }
                } else if (GAME.test(args[1])) {
                    if (TIMEOUT.test(args[2])) {
                        if (SET.test(args[0])) {
                            doSetGameTimeout(message, args[3], args[4])
                        } else if (UNSET.test(args[0])) {
                            doUnsetGameTimeout(message, args[3])
                        } else if (GET.test(args[0])) {
                            doGetGameTimeout(message, args[3])
                        } else {
                            doHelp(message, args[1])
                        }
                    } else {
                        doHelp(message, args[1])
                    }
                } else {
                    doHelp(message, cmd)
                }
            } else {
                doHelp(message, cmd)
            }
        }
        // update "lastMessage"
        updateLastMessage(message)
    }
}

function isRelevantMessage(message) {
    return (
        // message must be posted in a guild
        message.guild
        // ignore all bots
        && !message.author.bot
    )
}

function isBotCommand(message) {
    return (
        // message must contain a parsable string
        typeof message.content === "string"
        // bot must be addressed directly
        && message.content.startsWith("<@" + client.user.id + ">")
    )
}

function isAuthorModOrAdmin(message) {
    return (
        // guild owner can always perform any action (is always admin)
        message.member.id == message.guild.owner.id
        // bot superadmin can always perform any action (for debugging and support)
        || message.member.id == config.admin
        // check roles
        || hasRole(message.member, "mod")
        || hasRole(message.member, "admin")
    )
}

function isAuthorAdmin(message) {
    return (
        // guild owner can always perform any action (is always admin)
        message.member.id == message.guild.owner.id
        // bot admin can always perform any action (for debugging and support)
        || message.member.id == config.admin
        // check roles
        || hasRole(message.member, "admin")
    )
}

function hasRole(member, key) {
    const roles = db.get(member.guild.id, `roles.${key}`)
    return roles && roles.find(role => member.roles.has(role))
}

function doPing(message) {
    reply(message, "Pong!")
}

function doHelp(message, command) {
    reply(message, "you can find the manual here: <https://github.com/geotim90/geobot/blob/dev/README.md#commands>")
}

function doReport(message, user) {
    reply(message, ":head_bandage:")
}

function doSetContribution(message, user) {
    reply(message, ":head_bandage:")
}

function doGetContribution(message, user) {
    reply(message, ":head_bandage:")
}

function doUnsetContribution(message, user) {
    reply(message, ":head_bandage:")
}

function doAddRole(message, key, role) {
    reply(message, ":head_bandage:")
}

function doRemoveRole(message, key, role) {
    reply(message, ":head_bandage:")
}

function doGetRole(message, key) {
    reply(message, ":head_bandage:")
}

function doSetTimeout(message, key, days) {
    reply(message, ":head_bandage:")
}

function doGetTimeout(message, key) {
    reply(message, ":head_bandage:")
}

function doSetMember(message, key, member, timestamp) {
    reply(message, ":head_bandage:")
}

function doGetMember(message, key, member) {
    reply(message, ":head_bandage:")
}

function doSetGameTimeout(message, game, days) {
    reply(message, ":head_bandage:")
}

function doUnsetGameTimeout(message, game) {
    reply(message, ":head_bandage:")
}

function doGetGameTimeout(message, game) {
    reply(message, ":head_bandage:")
}

function reply(message, content) {
    message.reply(content)
        .then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
        .catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
}

function onPresenceUpdate(oldMember, newMember) {
    if (isRelevantPresenceUpdate(newMember)) {
        log("event", `guild=${newMember.guild.id}|member=${newMember.id}`, "presenceUpdate")
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

function isRelevantPresenceUpdate(member) {
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
        && member.presence.status === "online"
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
    log("update", `guild=${message.guild.id}|member=${message.author.id}`, "lastMessage", message.createdTimestamp)
    db.set(message.guild.id, message.createdTimestamp, `members.${message.author.id}.lastMessage`)
}

function updateLastOnline(member) {
    const now = new Date().getTime()
    log("update", `guild=${member.guild.id}|member=${member.id}`, "lastOnline", now)
    db.set(member.guild.id, now, `members.${member.id}.lastOnline`)
}

function updateLastPlaying(member) {
    const now = new Date().getTime()
    log("update", `guild=${member.guild.id}|member=${member.id}|game=${member.presence.game.applicationID}`, "lastPlaying", now)
    db.set(member.guild.id, now, `members.${member.id}.${member.presence.game.applicationID}`)
    updateGame(member.presence.game)
}

function updateGame(game) {
    log("update", `game=${game.applicationID}`, "->", game.name)
    db.set("games", game.name, game.applicationID)
}

function onGuildDelete(guild) {
    if (guild) {
        log("update", `guild=${guild.id}`, "guildDelete")
        db.delete(guild.id)
    }
}

function onGuildMemberRemove(member) {
    if (member.guild) {
        log("update", `guild=${member.guild.id}`, "guildMemberRemove", member.id)
        db.delete(member.guild.id, member.id)
    }
}

function onRoleDelete(role) {
    if (role.guild) {
        log("update", `guild=${role.guild.id}`, "roleDelete", role.id)
        db.remove(role.guild.id, role.id, "roles.initiate")
        db.remove(role.guild.id, role.id, "roles.member")
        db.remove(role.guild.id, role.id, "roles.mod")
        db.remove(role.guild.id, role.id, "roles.admin")
    }
}

// start client
client.login(config.token)
