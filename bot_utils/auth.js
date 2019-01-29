const { react } = require('./messaging')
const config = require('../config.json')

function checkAuthorization(msg, key, reaction = "✅") {
    console.log(`[TRACE] auth.checkAuthorization(${msg.id}, ${JSON.stringify(key)}, ${JSON.stringify(reaction)})`)
    const authorized =
        // guild owner can always perform any action
        msg.member.id == msg.guild.owner.id
        // bot admin can always perform any action (for debugging and support)
        || msg.member.id == config.admin

    if (!authorized) {
        react(msg, "🙉")
    } else if (reaction) {
        react(msg, reaction)
    }
    return authorized
}

module.exports = { checkAuthorization }
