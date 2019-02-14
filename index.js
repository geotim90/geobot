const config = require("./config.json");

const Discord = require("discord.js");
const Enmap = require("enmap");

// initialize Discord client
const client = new Discord.Client();

// initialize Enmap database
const db = new Enmap({
	name: "geobot",
	fetchAll: false,
	autoFetch: true,
	cloneLevel: "deep"
});

// logger
function log(type, context, message, obj) {
	if (context) {
		console.log(`${new Date().toISOString()} [${type.toUpperCase()}] {${context}} ${message}`, obj ? obj : "")
	} else {
		console.log(`${new Date().toISOString()} [${type.toUpperCase()}] ${message}`, obj ? obj : "")
	}
}

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
const LAST_ONLINE = /^lastOnline\b/i;
const LAST_MESSAGE = /^lastMessage\b/i;
const LAST_PLAYED = /^lastPlayed\b/i;

function onMessage(message) {
	if (isRelevantMessage(message)) {
		log("event", `guild=${message.guild.id}|message=${message.id}`, "message");
		if (isBotCommand(message) && isAuthorModOrAdmin(message)) {
			const admin = isAuthorAdmin(message);
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
				} else if (admin && UNSET.test(args[0])) {
					onUnsetContribution(message, args[2])
				} else {
					onHelp(message, args[1])
				}
			} else if (admin) {
				if (ROLE.test(args[1])) {
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
			} else {
				onHelp(message, cmd)
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
		message.member.id === message.guild.owner.id
		// bot superadmin can always perform any action (for debugging and support)
		|| message.member.id === config.admin
		// check roles
		|| hasRole(message.member, "mod")
		|| hasRole(message.member, "admin")
	)
}

function isAuthorAdmin(message) {
	return (
		// guild owner can always perform any action (is always admin)
		message.member.id === message.guild.owner.id
		// bot admin can always perform any action (for debugging and support)
		|| message.member.id === config.admin
		// check roles
		|| hasRole(message.member, "admin")
	)
}

function hasRole(member, key) {
	const roles = db.get(member.guild.id, `roles.${key}`);
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
		doReportGuild(message)
	}
}

function doReportMember(message, member) {
	if (member) {
		reply(message, ":head_bandage:")
	}
}

function doReportGuild(message) {
	reply(message, ":head_bandage:")
}

function onSetContribution(message, member) {
	doSetContribution(message, getMember(message, member))
}

function doSetContribution(message, member) {
	if (member) {
		db.set(message.guild.id, true, `members.${member.id}.contribution`);
		reply(message, `contribution by **${getName(member)}** set to ✅`)
	}
}

function onGetContribution(message, member) {
	doGetContribution(message, getMember(message, member))
}

function doGetContribution(message, member) {
	if (member) {
		const contribution = db.get(message.guild.id, `members.${member.id}.contribution`);
		if (contribution === true) {
			reply(message, `**${getName(member)}** has made a contribution 🎉`)
		} else {
			reply(message, `**${getName(member)}** has **not** made a contribution 😢`)
		}
	}
}

function onUnsetContribution(message, member) {
	doUnsetContribution(message, getMember(message, member))
}

function doUnsetContribution(message, member) {
	if (member) {
		db.delete(message.guild.id, `members.${member.id}.contribution`);
		reply(message, `contribution by **${getName(member)}** set to ❌`)
	}
}

function onAddRole(message, key, role) {
	doAddRole(message, getRoleKey(message, key), getRole(message, role))
}

function doAddRole(message, key, role) {
	if (key && role) {
		db.push(message.guild.id, role.id, `roles.${key}`);
		reply(message, `role **${role.name}** added as **`${key}`**`)
	}
}

function onRemoveRole(message, key, role) {
	doRemoveRole(message, getRoleKey(message, key), getRole(message, role))
}

function doRemoveRole(message, key, role) {
	if (key && role) {
		db.delete(message.guild.id, `roles.${key}.${role.id}`);
		reply(message, `role **${role.name}** removed as **`${key}`**`)
	}
}

function onGetRole(message, key) {
	doGetRole(message, getRoleKey(message, key))
}

function doGetRole(message, key) {
	if (key) {
		const roles = db.get(message.guild.id, `roles.${key}`);
		if (!roles || Object.keys(roles).length === 0) {
			reply(message, `no roles have been assigned to **${key}** 😢`)
		} else {
			reply(message, `I found ${Object.keys(roles).length} role(s) assigned to **${key}** 🎉`);
			let dump = "```css";
			Object.entries(roles).forEach(([k, v]) => dump = dump + `\n${v.id} - ${v.name}`);
			dump = dump + "```";
			send(message, dump);
		}
	}
}

function onSetTimeout(message, key, days) {
	doSetTimeout(message, getTimeoutKey(message, key), getDays(message, days))
}

function doSetTimeout(message, key, days) {
	if (key && days) {
		reply(message, ":head_bandage:")
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
		reply(message, ":head_bandage:")
	}
}

function doGetTimeoutAll(message) {
	reply(message, ":head_bandage:")
}

function onSetMember(message, key, member, gameOrTimestamp, timestamp) {
	if (timestamp) {
		doSetMemberGame(message, getMemberKey(message, key), getMember(message, member), getGame(message, gameOrTimestamp), getTimestamp(message, timestamp))
	} else {
		doSetMember(message, getMemberKey(message, key), getMember(message, member), getTimestamp(message, gameOrTimestamp))
	}
}

function doSetMemberGame(message, key, member, game, timestamp) {
	if (key && member && game && timestamp) {
		reply(message, ":head_bandage:")
	}
}

function doSetMember(message, key, member, timestamp) {
	if (key && member && timestamp) {
		reply(message, ":head_bandage:")
	}
}

function onUnsetMember(message, key, member, game) {
	if (game) {
		doUnsetMemberGame(message, getMemberKey(message, key), getMember(message, member), getGame(message, game))
	} else {
		doUnsetMember(message, getMemberKey(message, key), getMember(message, member))
	}
}

function doUnsetMemberGame(message, key, member, game) {
	if (key && member && game) {
		reply(message, ":head_bandage:")
	}
}

function doUnsetMember(message, key, member) {
	if (key && member) {
		reply(message, ":head_bandage:")
	}
}

function onGetMember(message, key, member) {
	doGetMember(message, getMemberKey(message, key), getMember(message, member))
}

function doGetMember(message, key, member) {
	if (key && member) {
		reply(message, ":head_bandage:")
	}
}

function onSetGameTimeout(message, game, days) {
	doSetGameTimeout(message, getGame(message, game), getDays(message, days))
}

function doSetGameTimeout(message, game, days) {
	if (game && days) {
		reply(message, ":head_bandage:")
	}
}

function onUnsetGameTimeout(message, game) {
	doUnsetGameTimeout(message, getGame(message, game))
}

function doUnsetGameTimeout(message, game) {
	if (game) {
		reply(message, ":head_bandage:")
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
		reply(message, ":head_bandage:")
	}
}

function doGetGameTimeoutAll(message) {
	reply(message, ":head_bandage:")
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
		dump = dump + "```";
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
		dump = dump + "```";
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
	if (LAST_ONLINE.test(key)) {
		return "lastOnline"
	} else if (LAST_MESSAGE.test(key)) {
		return "lastMessage"
	} else if (LAST_PLAYED.test(key)) {
		return "lastPlayed"
	} else {
		reply(message, "you did not specify one of `lastOnline`, `lastMessage` or `lastPlayed` 😟");
		return false
	}
}

function getGame(message, input) {
	if (!input) {
		reply(message, "you did not provide a game name or application ID 😟");
		return false
	}
	if (/^\d+$/.test(input)) {
		const game = db.get("games", input);
		if (game) {
			return { applicationID: input, name: game }
		}
	}
	const search = input.toLowerCase();
	const game = Object.entries(db.get("games")).filter(([k, v]) => v.toLowerCase().includes(search)).map(([k, v]) => ({ applicationID: k, name: v }));
	if (game.length < 1) {
		reply(message, "I couldn't find any game matching your input `" + Discord.Util.escapeMarkdown(input) + "` 😟");
		return false
	} else if (game.length > 1) {
		reply(message, "I found " + game.length + " games matching your input `" + Discord.Util.escapeMarkdown(input) + "` 🤔");
		let dump = "```css";
		game.forEach(e => dump = dump + `\n${e.applicationID} - ${e.name}`);
		dump = dump + "```";
		send(message, dump);
		return false
	} else {
		return game[0]
	}
}

function getTimestamp(message, input) {
	if (/^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}(:\d{2}(\.\d{3})?)?)?Z?$/.test(input)) {
		return new Date(input)
	} else {
		reply(message, "you did not provide an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`) 😟");
		return false
	}
}

function getName(member) {
	if (member.nickname) {
		return member.nickname
	} else {
		return member.user.username
	}
}

function reply(message, content) {
	message.reply(content)
		.then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
		.catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
}

function send(message, content) {
	message.channel.send(content)
		.then(out => log("reply", `guild=${message.guild.id}|message=${message.id}`, `with message ${out.id}`, JSON.stringify(out.content)))
		.catch(error => log("reply", `guild=${message.guild.id}|message=${message.id}`, "[ERROR]", error.message))
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
	db.set(message.guild.id, message.createdTimestamp, `members.${message.author.id}.lastMessage`)
}

function updateLastOnline(member) {
	const now = new Date().getTime();
	log("update", `guild=${member.guild.id}|member=${member.id}`, "lastOnline", now);
	db.set(member.guild.id, now, `members.${member.id}.lastOnline`)
}

function updateLastPlaying(member) {
	const now = new Date().getTime();
	log("update", `guild=${member.guild.id}|member=${member.id}|game=${member.presence.game.applicationID}`, "lastPlaying", now);
	db.set(member.guild.id, now, `members.${member.id}.${member.presence.game.applicationID}`);
	updateGame(member.presence.game)
}

function updateGame(game) {
	log("update", `game=${game.applicationID}`, "->", game.name);
	db.set("games", game.name, game.applicationID)
}

function onGuildDelete(guild) {
	if (guild) {
		log("update", `guild=${guild.id}`, "guildDelete");
		db.delete(guild.id)
	}
}

function onGuildMemberRemove(member) {
	if (member.guild) {
		log("update", `guild=${member.guild.id}`, "guildMemberRemove", member.id);
		db.delete(member.guild.id, member.id)
	}
}

function onRoleDelete(role) {
	if (role.guild) {
		log("update", `guild=${role.guild.id}`, "roleDelete", role.id);
		db.remove(role.guild.id, role.id, "roles.initiate");
		db.remove(role.guild.id, role.id, "roles.member");
		db.remove(role.guild.id, role.id, "roles.mod");
		db.remove(role.guild.id, role.id, "roles.admin")
	}
}

// start client
client.login(config.token)
	.then(out => log("health", false, "LOGIN", out))
	.catch(error => log("health", false, "[ERROR]", error.toString()));
