const config = require("./config.json");

const {log} = require("./log.js");
const {db_get, db_set, db_delete, db_has, db_push, db_remove} = require("./data.js");

// initialize Discord client
const Discord = require("discord.js");
const client = new Discord.Client();

// track bot health
client.on("debug", info => log("health", false, "[DEBUG]", info));
client.on("disconnect", event => log("health", false, "DISCONNECT", event));
client.on("error", event => log("health", false, "[ERROR]", event.message));
client.on("rateLimit", rateLimitInfo => log("health", false, "RATE LIMIT", rateLimitInfo));
client.on("ready", () => log("health", false, "READY"));
client.on("reconnecting", () => log("health", false, "RECONNECTING"));
client.on("resume", replayed => log("health", false, "RESUME", replayed));
client.on("warn", info => log("health", false, "[WARN]", info));

// track guild member activity
client.on("message", onMessage);
client.on("presenceUpdate", onPresenceUpdate);
client.on("guildMemberUpdate", onGuildMemberUpdate);

// track removals for database clean-up
client.on("guildDelete", onGuildDelete);
client.on("guildMemberRemove", onGuildMemberRemove);
client.on("roleDelete", onRoleDelete);

// process commands
const PING = /^ping\b/i;
const HELP = /^help\b/i;
const REPORT = /^report\b/i;
const CONTRIBUTION = /^contribution\b/i;
const SET = /^set\b/i;
const GET = /^get\b/i;
const UNSET = /^unset\b/i;
const ROLE = /^role\b/i;
const ADD = /^add\b/i;
const REMOVE = /^remove\b/i;
const TIMEOUT = /^timeout\b/i;
const MEMBER = /^member\b/i;
const GAME = /^game\b/i;
const INITIATE = /^initiate\b/i;
const MOD = /^mod\b/i;
const ADMIN = /^admin\b/i;
const JOINED = /^joined\b/i;
const LAST_ONLINE = /^lastOnline\b/i;
const LAST_MESSAGE = /^lastMessage\b/i;
const LAST_PLAYED = /^lastPlayed\b/i;

function onMessage(message) {
    if (isRelevantMessage(message)) {
        log("event", `guild=${message.guild.id}|message=${message.id}`, "message");
        if (isBotCommand(message) && isBotCommandChannel(message) && isModOrAdmin(message.member)) {
            // extract cmd and args from message
            const cmd = message.content.substring(message.content.indexOf('>') + 1).trim();
            const args = cmd.split(/\s+/g);
            // process command
            log("command", `guild=${message.guild.id}|message=${message.id}`, JSON.stringify(cmd));
            if (PING.test(cmd)) {
                onPing(message)
            } else if (HELP.test(cmd)) {
                onHelp(message, args[1])
            } else if (REPORT.test(cmd)) {
                onReport(message, args[1])
            } else if (CONTRIBUTION.test(args[1])) {
                if (SET.test(args[0])) {
                    onSetContribution(message, args[2])
                } else if (GET.test(args[0])) {
                    onGetContribution(message, args[2])
                } else if (UNSET.test(args[0])) {
                    onUnsetContribution(message, args[2])
                } else {
                    onHelp(message, args[1])
                }
            } else if (ROLE.test(args[1])) {
                if (ADD.test(args[0])) {
                    onAddRole(message, args[2], args[3])
                } else if (REMOVE.test(args[0])) {
                    onRemoveRole(message, args[2], args[3])
                } else if (GET.test(args[0])) {
                    onGetRole(message, args[2], args[3])
                } else {
                    onHelp(message, args[1])
                }
            } else if (TIMEOUT.test(args[1])) {
                if (SET.test(args[0])) {
                    onSetTimeout(message, args[2], args[3])
                } else if (UNSET.test(args[0])) {
                    onUnsetTimeout(message, args[2])
                } else if (GET.test(args[0])) {
                    onGetTimeout(message, args[2])
                } else {
                    onHelp(message, args[1])
                }
            } else if (MEMBER.test(args[1])) {
                if (SET.test(args[0])) {
                    onSetMember(message, args[2], args[3], args[4], args[5])
                } else if (UNSET.test(args[0])) {
                    onUnsetMember(message, args[2], args[3], args[4])
                } else if (GET.test(args[0])) {
                    onGetMember(message, args[2], args[3], args[4])
                } else {
                    onHelp(message, args[1])
                }
            } else if (GAME.test(args[1])) {
                if (TIMEOUT.test(args[2])) {
                    if (SET.test(args[0])) {
                        onSetGameTimeout(message, args[3], args[4])
                    } else if (UNSET.test(args[0])) {
                        onUnsetGameTimeout(message, args[3])
                    } else if (GET.test(args[0])) {
                        onGetGameTimeout(message, args[3])
                    } else {
                        onHelp(message, args[1])
                    }
                } else {
                    onHelp(message, args[1])
                }
            } else {
                onHelp(message, cmd)
            }
        }
        // update activity
        updateLastMessage(message);
        updateLastOnline(message.member, true)
    }
}

function isRelevantMessage(message) {
    return (
        // message must be posted in a guild
        message.guild
        // ignore all bots
        && !message.author.bot
        // check for devmode
        && (!config.devmode || message.author.id === config.admin)
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

function isBotCommandChannel(message) {
    return (
        // bot must have permission to send messages in given channel
        message.channel.permissionsFor(message.guild.me).has("SEND_MESSAGES")
    )
}

function isModOrAdmin(member) {
    return hasRole(member, "mod")
        || hasRole(member, "admin")
}

function isAdmin(member) {
    return hasRole(member, "admin")
}

function hasRole(member, key) {
    if (key === "admin") {
        if (
            // guild owner can always perform any action (is always admin)
            member.id === member.guild.owner.id
            // bot admin can always perform any action (for debugging and support)
            || member.id === config.admin
        ) {
            return true
        }
    }
    const roles = db_get(member.guild.id, `roles.${key}`);
    return roles && roles.find(role => member.roles.has(role))
}

function onPing(message) {
    reply(message, "Pong!")
}

function onHelp(message, command) {
    reply(message, "you can find the manual here: <https://github.com/geotim90/geobot/blob/dev/README.md#commands>")
}

function onReport(message, member) {
    if (member) {
        doReportMember(message, getMember(message, member))
    } else {
        doReport(message)
    }
}

function doReportMember(message, member) {
    if (member) {
        const data = db_get(message.guild.id, `members.${member.id}`);
        let result = `this is what I have on **${getName(member)}** (${member.id})`;
        result += `\n\n__**Roles**__`;
        result += `\n${hasRole(member, "initiate") ? "✅" : "❌"} Initiate`;
        result += `\n${hasRole(member, "member") ? "✅" : "❌"} Member`;
        result += `\n${hasRole(member, "mod") ? "✅" : "❌"} Mod`;
        result += `\n${hasRole(member, "admin") ? "✅" : "❌"} Admin`;
        result += `\n\n__**Activity**__`;
        if (data) {
            result += `\n${data["joined"] ? "✅" : (hasRole(member, "initiate") ? "❌" : "⚠️")} Joined: ${formatDaysAgo(data["joined"])}`;
            result += `\n${data["contribution"] ? "✅" : (!hasRole(member, "initiate") || getDaysAgo(data["joined"]) < getTimeout(message.guild, "contribution") ? "⚠️" : "❌")} Contribution: **${data["contribution"] ? "yes" : "no"}**`;
            result += `\n${getDaysAgo(data["lastOnline"]) < (getTimeout(message.guild, "lastOnline") || Infinity) ? "✅" : (!hasRole(member, "member") ? "⚠️" : "❌")} Last online: ${formatDaysAgo(data["lastOnline"])}`;
            result += `\n${getDaysAgo(data["lastMessage"]) < (getTimeout(message.guild, "lastMessage") || Infinity) ? "✅" : (!hasRole(member, "member") ? "⚠️" : "❌")} Last message: ${formatDaysAgo(data["lastMessage"])}`;
            const applicationIDs = db_get(message.guild.id, "timeouts");
            if (applicationIDs) {
                const games = Object.keys(applicationIDs).filter(applicationID => /^\d+$/.test(applicationID)).map(applicationID => getGame(message, applicationID));
                games.forEach(game => result += `\n${getDaysAgo(data[game.applicationID]) < (getTimeout(message.guild, game.applicationID) || Infinity) ? "✅" : (!hasRole(member, "member") ? "⚠️" : "❌")} Last played **${game.name}** (${game.applicationID}): ${formatDaysAgo(data[game.applicationID])}`);
            }
        } else {
            result += `\n**undefined**`
        }
        reply(message, result)
    }
}

function doReport(message) {
    const initiates = message.guild.members.filter(member => hasRole(member, "initiate"));
    let report1 = "__**Initiates that have made a contribution**__";
    let report2 = "__**Initiates that have not made a contribution**__";
    for (const member of initiates.values()) {
        const joined = db_get(message.guild.id, `members.${member.id}.joined`);
        const contribution = db_get(message.guild.id, `members.${member.id}.contribution`);
        const timeout = getTimeout(message.guild, "contribution");
        if (contribution && getDays(joined) < timeout) {
            report1 += `\n✔️ **${getName(member)}** joined ${formatDaysAgo(joined, false)}`
        } else if (contribution) {
            report1 += `\n✅ **${getName(member)}** joined ${formatDaysAgo(joined, false)}`
        } else if (getDaysAgo(joined) < timeout) {
            report2 += `\n⚠️ **${getName(member)}** joined ${formatDaysAgo(joined, false)}`
        } else {
            report2 += `\n❌ **${getName(member)}** joined ${formatDaysAgo(joined, false)}`
        }
    }
    const members = message.guild.members.filter(member => hasRole(member, "member"));
    const timeoutLastOnline = getTimeout(message.guild, "lastOnline");
    const timeoutLastMessage = getTimeout(message.guild, "lastMessage");
    const games = Object.keys(db_get(message.guild.id, "timeouts") || {})
        .filter(applicationID => /^\d+$/.test(applicationID))
        .map(applicationID => ({
            ...getGame(message, applicationID),
            "timeout": getTimeout(message.guild, applicationID)
        }));
    let report3 = "__**Members that are completely inactive**__";
    let report4 = "__**Members that are partially inactive**__";
    for (const member of members.values()) {
        const data = db_get(message.guild.id, `members.${member.id}`);
        if (data) {
            let anyActive = false;
            let anyInactive = false;
            let minTimeout = {};
            let maxTimeout = {};
            if (timeoutLastOnline) {
                const lastOnline = getDaysAgo(data["lastOnline"]);
                anyActive |= lastOnline < timeoutLastOnline;
                anyInactive |= isNaN(lastOnline) || lastOnline >= timeoutLastOnline;
                minTimeout = {activity: "was last online", days: lastOnline, timestamp: data["lastOnline"]};
                maxTimeout = {activity: "was last online", days: lastOnline, timestamp: data["lastOnline"]};
            }
            if (timeoutLastMessage) {
                const lastMessage = getDaysAgo(data["lastMessage"]);
                anyActive |= lastMessage < timeoutLastMessage;
                anyInactive |= isNaN(lastMessage) || lastMessage >= timeoutLastMessage;
                if (lastMessage < (minTimeout.days || Infinity)) minTimeout = {
                    activity: "last messaged",
                    days: lastMessage,
                    timestamp: data["lastMessage"]
                };
                if (lastMessage > (maxTimeout.days || -Infinity)) maxTimeout = {
                    activity: "last messaged",
                    days: lastMessage,
                    timestamp: data["lastMessage"]
                };
            }
            for (const game of games) {
                if (game.timeout) {
                    const lastPlayed = getDaysAgo(data[game.applicationID]);
                    anyActive |= lastPlayed < game.timeout;
                    anyInactive |= isNaN(lastPlayed) || lastPlayed >= game.timeout;
                    if (lastPlayed < (minTimeout.days || Infinity)) minTimeout = {
                        activity: `last played **${game.name}** (${game.applicationID})`,
                        days: lastPlayed,
                        timestamp: data[game.applicationID]
                    };
                    if (lastPlayed > (maxTimeout.days || -Infinity)) maxTimeout = {
                        activity: `last played **${game.name}** (${game.applicationID})`,
                        days: lastPlayed,
                        timestamp: data[game.applicationID]
                    };
                }
            }
            if (anyInactive) {
                if (anyActive) {
                    report4 += `\n⚠️ **${getName(member)}** ${maxTimeout.activity} ${formatDaysAgo(maxTimeout.timestamp, false)}`
                } else {
                    report3 += `\n❌ **${getName(member)}** ${minTimeout.activity} ${formatDaysAgo(minTimeout.timestamp, false)}`
                }
            }
        }
    }
    send(message, report1);
    send(message, report2);
    send(message, report3);
    send(message, report4)
}

function onSetContribution(message, member) {
    doSetContribution(message, getMember(message, member))
}

function doSetContribution(message, member) {
    if (member) {
        db_set(message.guild.id, true, `members.${member.id}.contribution`);
        reply(message, `set contribution for **${getName(member)}** (${member.id})`)
    }
}

function onGetContribution(message, member) {
    doGetContribution(message, getMember(message, member))
}

function doGetContribution(message, member) {
    if (member) {
        const contribution = db_get(message.guild.id, `members.${member.id}.contribution`);
        if (contribution === true) {
            reply(message, `**${getName(member)}** (${member.id}) has made a contribution 🎉`)
        } else {
            reply(message, `**${getName(member)}** (${member.id}) has **not** made a contribution 😢`)
        }
    }
}

function onUnsetContribution(message, member) {
    if (requireAdmin(message)) {
        doUnsetContribution(message, getMember(message, member))
    }
}

function doUnsetContribution(message, member) {
    if (member) {
        db_delete(message.guild.id, `members.${member.id}.contribution`);
        reply(message, `removed contribution for **${getName(member)}** (${member.id})`)
    }
}

function onAddRole(message, key, role) {
    if (requireAdmin(message)) {
        doAddRole(message, getRoleKey(message, key), getRole(message, role))
    }
}

function doAddRole(message, key, role) {
    if (key && role) {
        if (!db_has(message.guild.id, `roles.${key}`)) {
            db_set(message.guild.id, [], `roles.${key}`)
        }
        db_push(message.guild.id, role.id, `roles.${key}`);
        reply(message, `added **${key}** role **${role.name}** (${role.id})`);
        if (key === "initiate") {
            role.members.forEach(updateJoined)
        }
    }
}

function onRemoveRole(message, key, role) {
    if (requireAdmin(message)) {
        doRemoveRole(message, getRoleKey(message, key), getRole(message, role))
    }
}

function doRemoveRole(message, key, role) {
    if (key && role) {
        db_remove(message.guild.id, role.id, `roles.${key}`);
        reply(message, `removed **${key}** role **${role.name}** (${role.id})`)
    }
}

function onGetRole(message, key) {
    if (requireAdmin(message)) {
        doGetRole(message, getRoleKey(message, key))
    }
}

function doGetRole(message, key) {
    if (key) {
        const roles = db_get(message.guild.id, `roles.${key}`);
        if (!roles || roles.length === 0) {
            reply(message, `no roles are assigned to **${key}**`)
        } else {
            reply(message, `${Object.keys(roles).length} roles are assigned to **${key}**`);
            let dump = "```css";
            roles.map(id => message.guild.roles.get(id)).forEach(role => dump = dump + `\n${role.id} - ${role.name}`);
            dump = dump + "\n```";
            send(message, dump);
        }
    }
}

function onSetTimeout(message, key, days) {
    if (requireAdmin(message)) {
        doSetTimeout(message, getTimeoutKey(message, key), getDays(message, days))
    }
}

function doSetTimeout(message, key, days) {
    if (key && days) {
        db_set(message.guild.id, days, `timeouts.${key}`);
        reply(message, `set timeout for **${key}** to **${days} days**`)
    }
}

function onUnsetTimeout(message, key) {
    if (requireAdmin(message)) {
        doUnsetTimeout(message, getTimeoutKey(message, key))
    }
}

function doUnsetTimeout(message, key) {
    if (key) {
        db_delete(message.guild.id, `timeouts.${key}`);
        reply(message, `removed timeout for **${key}**`)
    }
}

function onGetTimeout(message, key) {
    if (key) {
        doGetTimeout(message, getTimeoutKey(message, key))
    } else {
        doGetTimeoutAll(message);
        doGetGameTimeoutAll(message)
    }
}

function doGetTimeout(message, key) {
    if (key) {
        const days = db_get(message.guild.id, `timeouts.${key}`);
        if (days) {
            reply(message, `timeout for **${key}** is **${days} days**`)
        } else {
            reply(message, `timeout for **${key}** is **undefined**`)
        }
    }
}

function doGetTimeoutAll(message) {
    const daysContribution = db_get(message.guild.id, "timeouts.contribution");
    const daysLastOnline = db_get(message.guild.id, "timeouts.lastOnline");
    const daysLastMessage = db_get(message.guild.id, "timeouts.lastMessage");
    reply(message, `timeout for **contribution** is **${daysContribution ? daysContribution + " days" : "undefined"}**, `
        + `for **lastOnline** is **${daysLastOnline ? daysLastOnline + " days" : "undefined"}**, `
        + `for **lastMessage** is **${daysLastMessage ? daysLastMessage + " days" : "undefined"}**`)
}

function onSetMember(message, key, member, gameOrTimestamp, timestamp) {
    if (requireAdmin(message)) {
        if (LAST_PLAYED.test(key)) {
            doSetMemberGame(message, getMemberKey(message, key), getMember(message, member), getGame(message, gameOrTimestamp), getTimestamp(message, timestamp))
        } else {
            doSetMember(message, getMemberKey(message, key), getMember(message, member), getTimestamp(message, gameOrTimestamp))
        }
    }
}

function doSetMemberGame(message, key, member, game, timestamp) {
    if (key && member && game && timestamp) {
        db_set(message.guild.id, timestamp, `members.${member.id}.${game.applicationID}`);
        reply(message, `set **${key}** for **${getName(member)}** (${member.id}) in **${game.name}** (${game.applicationID}) to **${formatTimestamp(timestamp)}**`)
    }
}

function doSetMember(message, key, member, timestamp) {
    if (key && member && timestamp) {
        db_set(message.guild.id, timestamp, `members.${member.id}.${key}`);
        reply(message, `set **${key}** for **${getName(member)}** (${member.id}) to **${formatTimestamp(timestamp)}**`)
    }
}

function onUnsetMember(message, key, member, game) {
    if (requireAdmin(message)) {
        if (LAST_PLAYED.test(key)) {
            doUnsetMemberGame(message, getMemberKey(message, key), getMember(message, member), getGame(message, game))
        } else {
            doUnsetMember(message, getMemberKey(message, key), getMember(message, member))
        }
    }
}

function doUnsetMemberGame(message, key, member, game) {
    if (key && member && game) {
        db_delete(message.guild.id, `members.${member.id}.${game.applicationID}`);
        reply(message, `removed **${key}** for **${getName(member)}** (${member.id}) in **${game.name}** (${game.applicationID})`)
    }
}

function doUnsetMember(message, key, member) {
    if (key && member) {
        db_delete(message.guild.id, `members.${member.id}.${key}`);
        reply(message, `removed **${key}** for **${getName(member)}** (${member.id})`)
    }
}

function onGetMember(message, key, member, game) {
    if (LAST_PLAYED.test(key)) {
        doGetMemberGame(message, getMemberKey(message, key), getMember(message, member), getGame(message, game))
    } else {
        doGetMember(message, getMemberKey(message, key), getMember(message, member))
    }
}

function doGetMemberGame(message, key, member, game) {
    if (key && member && game) {
        const timestamp = db_get(message.guild.id, `members.${member.id}.${game.applicationID}`);
        reply(message, `**${key}** for **${getName(member)}** (${member.id}) in **${game.name}** (${game.applicationID}) is **${formatTimestamp(timestamp)}**`)
    }
}

function doGetMember(message, key, member) {
    if (key && member) {
        const timestamp = db_get(message.guild.id, `members.${member.id}.${key}`);
        reply(message, `**${key}** for **${getName(member)}** (${member.id}) is **${formatTimestamp(timestamp)}**`)
    }
}

function onSetGameTimeout(message, game, days) {
    if (requireAdmin(message)) {
        doSetGameTimeout(message, getGame(message, game), getDays(message, days))
    }
}

function doSetGameTimeout(message, game, days) {
    if (game && days) {
        db_set(message.guild.id, days, `timeouts.${game.applicationID}`);
        reply(message, `set timeout for **${game.name}** (${game.applicationID}) to **${days} days**`)
    }
}

function onUnsetGameTimeout(message, game) {
    if (requireAdmin(message)) {
        doUnsetGameTimeout(message, getGame(message, game))
    }
}

function doUnsetGameTimeout(message, game) {
    if (game) {
        db_delete(message.guild.id, `timeouts.${game.applicationID}`);
        reply(message, `removed timeout for **${game.name}** (${game.applicationID})`)
    }
}

function onGetGameTimeout(message, game) {
    if (game) {
        doGetGameTimeout(message, getGame(message, game))
    } else {
        doGetGameTimeoutAll(message)
    }
}

function doGetGameTimeout(message, game) {
    if (game) {
        const days = db_get(message.guild.id, `timeouts.${game.applicationID}`);
        if (days) {
            reply(message, `timeout for **${game.name}** (${game.applicationID}) is **${days} days**`)
        } else {
            reply(message, `timeout for **${game.name}** (${game.applicationID}) is **undefined**`)
        }
    }
}

function doGetGameTimeoutAll(message) {
    const timeouts = db_get(message.guild.id, "timeouts");
    if (timeouts) {
        const games = Object.keys(timeouts).filter(key => /^\d+$/.test(key)).map(applicationID => getGame(message, applicationID));
        if (games.length > 0) {
            reply(message, `timeout for **lastPlayed** is set for ${games.length} games`);
            let dump = "```css";
            games.forEach(game => dump = dump + `\n${game.applicationID} - ${game.name} - ${timeouts[game.applicationID]} days`);
            dump = dump + "\n```";
            send(message, dump)
        } else {
            reply(message, `timeout for **lastPlayed** is **undefined**`)
        }
    } else {
        reply(message, `timeout for **lastPlayed** is **undefined**`)
    }
}

function requireAdmin(message) {
    if (isAdmin(message.member)) {
        return true
    } else {
        reply(message, "you do not have permission to use this command 😟");
        return false
    }
}

function getMember(message, input) {
    if (!input) {
        reply(message, "you did not provide a user mention, user name or user ID 😟");
        return false
    }
    if (/^\d+$/.test(input)) {
        const member = message.guild.members.get(input);
        if (member) {
            return member
        }
    }
    if (/^<@\d+>$/.test(input)) {
        const member = message.guild.members.get(input.slice(2, -1));
        if (member) {
            return member
        }
    }
    const search = input.toLowerCase();
    const member = message.guild.members.filter(e =>
        !e.user.bot && (
        (e.nickname && e.nickname.toLowerCase().includes(search))
        || (e.user.username && e.user.username.toLowerCase().includes(search))
        || (e.user.tag && e.user.tag.toLowerCase().includes(search)))
    );
    if (member.size < 1) {
        reply(message, "I couldn't find any guild member matching your input `" + Discord.Util.escapeMarkdown(input) + "` 😟");
        return false
    } else if (member.size > 1) {
        reply(message, "I found " + member.size + " guild members matching your input `" + Discord.Util.escapeMarkdown(input) + "` 🤔");
        let dump = "```css";
        member.forEach(e => dump = dump + `\n${e.id} - ${getName(e)} / ${e.user.tag}`);
        dump = dump + "\n```";
        send(message, dump);
        return false
    } else {
        return member.first()
    }
}

function getRoleKey(message, input) {
    if (INITIATE.test(input)) {
        return "initiate"
    } else if (MEMBER.test(input)) {
        return "member"
    } else if (MOD.test(input)) {
        return "mod"
    } else if (ADMIN.test(input)) {
        return "admin"
    } else {
        reply(message, "you need to specify one of `initiate`, `member`, `mod` or `admin` 😟");
        return false
    }
}

function getRole(message, input) {
    if (!input) {
        reply(message, "you did not provide a role mention, role name or role ID 😟");
        return false
    }
    if (/^\d+$/.test(input)) {
        const role = message.guild.roles.get(input);
        if (role) {
            return role
        }
    }
    if (/^<@&\d+>$/.test(input)) {
        const role = message.guild.roles.get(input.slice(3, -1));
        if (role) {
            return role
        }
    }
    const search = input.toLowerCase();
    const role = message.guild.roles.filter(e => e.name && e.name.toLowerCase().includes(search));
    if (role.size < 1) {
        reply(message, "I couldn't find any guild role matching your input `" + Discord.Util.escapeMarkdown(input) + "` 😟");
        return false
    } else if (role.size > 1) {
        reply(message, "I found " + role.size + " guild roles matching your input `" + Discord.Util.escapeMarkdown(input) + "` 🤔");
        let dump = "```css";
        role.forEach(e => dump = dump + `\n${e.id} - ${e.name}`);
        dump = dump + "\n```";
        send(message, dump)
    } else {
        return role.first()
    }
}

function getTimeoutKey(message, input) {
    if (CONTRIBUTION.test(input)) {
        return "contribution"
    } else if (LAST_ONLINE.test(input)) {
        return "lastOnline"
    } else if (LAST_MESSAGE.test(input)) {
        return "lastMessage"
    } else {
        reply(message, "you did not specify one of `contribution`, `lastOnline` or `lastMessage` 😟");
        return false
    }
}

function getDays(message, input) {
    if (/\d{1,3}/) {
        const value = parseInt(input);
        if (value >= 1 || value <= 365) {
            return value
        }
    }
    reply(message, "you did not specify a positive integer between `1` (1 day) and `365` (1 year) 😟");
    return false
}

function getMemberKey(message, key) {
    if (JOINED.test(key)) {
        return "joined"
    } else if (LAST_ONLINE.test(key)) {
        return "lastOnline"
    } else if (LAST_MESSAGE.test(key)) {
        return "lastMessage"
    } else if (LAST_PLAYED.test(key)) {
        return "lastPlayed"
    } else {
        reply(message, "you did not specify one of `joined`, `lastOnline`, `lastMessage` or `lastPlayed` 😟");
        return false
    }
}

function getGame(message, input) {
    if (!input) {
        reply(message, "you did not provide a game name or application ID 😟");
        return false
    }
    if (/^\d+$/.test(input)) {
        const game = db_get("games", input);
        if (game) {
            return {applicationID: input, name: game}
        }
    }
    const search = input.toLowerCase();
    const game = Object.entries(db_get("games")).filter(([k, v]) => v.toLowerCase().includes(search)).map(([k, v]) => ({
        applicationID: k,
        name: v
    }));
    if (game.length < 1) {
        reply(message, "I couldn't find any game matching your input `" + Discord.Util.escapeMarkdown(input) + "` 😟");
        return false
    } else if (game.length > 1) {
        reply(message, "I found " + game.length + " games matching your input `" + Discord.Util.escapeMarkdown(input) + "` 🤔");
        let dump = "```css";
        game.forEach(e => dump = dump + `\n${e.applicationID} - ${e.name}`);
        dump = dump + "\n```";
        send(message, dump);
        return false
    } else {
        return game[0]
    }
}

function getTimestamp(message, input) {
    if (/^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}(:\d{2}(\.\d{3})?)?)?Z?$/.test(input)) {
        return new Date(input).valueOf()
    } else {
        reply(message, "you did not provide an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`) 😟");
        return false
    }
}

function getName(member) {
    if (member.nickname) {
        return member.nickname
    } else if (member.user) {
        return member.user.username
    }
}

function formatDaysAgo(timestamp, includeTimestamp = true) {
    if (/^\d+$/.test(timestamp)) {
        if (includeTimestamp) {
            return `**${getDaysAgo(timestamp)} days ago** (${formatTimestamp(timestamp)})`
        } else {
            return `**${getDaysAgo(timestamp)} days ago**`
        }
    } else {
        return "**undefined**"
    }
}

function getDaysAgo(timestamp) {
    const dayMillis = 1000 * 60 * 60 * 24;
    const now = new Date().getTime();
    return Math.floor((now - timestamp) / dayMillis)
}

function formatTimestamp(timestamp) {
    if (!timestamp) {
        return "undefined"
    }
    const string = new Date(timestamp).toISOString();
    if (string.endsWith("T00:00:00.000Z")) {
        return string.substring(0, string.lastIndexOf('T'))
    } else {
        return string
    }
}

function getTimeout(guild, key) {
    return guild && guild.id && key ? db_get(guild.id, `timeouts.${key}`) || 0 : 0;
}

function reply(message, content) {
    if (config.devmode) {
        message.author.send(abbreviate(content, 2000 - 23))
            .then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
            .catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
    } else {
        message.reply(abbreviate(content, 2000 - 23))
            .then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
            .catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
    }
}

function send(message, content) {
    if (config.devmode) {
        message.author.send(abbreviate(content, 2000))
            .then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
            .catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
    } else {
        message.channel.send(abbreviate(content, 2000))
            .then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
            .catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
    }
}

function abbreviate(string, limit) {
    if (limit && string.length > limit) {
        return string.substring(0, limit - 4).trim() + ' ...'
    } else {
        return string
    }
}

function onPresenceUpdate(oldMember, newMember) {
    if (isRelevantPresenceUpdate(newMember)) {
        log("event", `guild=${newMember.guild.id}|member=${newMember.id}`, "presenceUpdate");
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
    log("update", `guild=${message.guild.id}|member=${message.author.id}`, "lastMessage", message.createdTimestamp);
    db_set(message.guild.id, message.createdTimestamp, `members.${message.author.id}.lastMessage`)
}

function updateLastOnline(member, performedAction = false) {
    if (performedAction || member.presence.status === "online") {
        const now = new Date().getTime();
        log("update", `guild=${member.guild.id}|member=${member.id}`, "lastOnline", now);
        db_set(member.guild.id, now, `members.${member.id}.lastOnline`)
    }
}

function updateLastPlaying(member) {
    const now = new Date().getTime();
    log("update", `guild=${member.guild.id}|member=${member.id}|game=${member.presence.game.applicationID}`, "lastPlaying", now);
    db_set(member.guild.id, now, `members.${member.id}.${member.presence.game.applicationID}`);
    updateGame(member.presence.game)
}

function updateGame(game) {
    log("update", `game=${game.applicationID}`, "->", game.name);
    db_set("games", game.name, game.applicationID)
}

function onGuildMemberUpdate(oldMember, newMember) {
    if (hasRole(newMember, "initiate")) {
        updateJoined(newMember)
    }
}

function updateJoined(member) {
    if (!db_has(member.guild.id, `members.${member.id}.joined`)) {
        const now = new Date().getTime();
        log("update", `guild=${member.guild.id}|member=${member.id}`, "joined", now);
        db_set(member.guild.id, now, `members.${member.id}.joined`)
    }
}

function onGuildDelete(guild) {
    if (guild) {
        log("update", `guild=${guild.id}`, "guildDelete");
        db_delete(guild.id)
    }
}

function onGuildMemberRemove(member) {
    if (member.guild) {
        log("update", `guild=${member.guild.id}`, "guildMemberRemove", member.id);
        db_delete(member.guild.id, member.id)
    }
}

function onRoleDelete(role) {
    if (role.guild) {
        log("update", `guild=${role.guild.id}`, "roleDelete", role.id);
        db_remove(role.guild.id, role.id, "roles.initiate");
        db_remove(role.guild.id, role.id, "roles.member");
        db_remove(role.guild.id, role.id, "roles.mod");
        db_remove(role.guild.id, role.id, "roles.admin")
    }
}

// start client
client.login(config.token)
    .then(out => log("health", false, "LOGIN", out))
    .catch(error => log("health", false, "[ERROR]", error.toString()));
