const config = require('./config.json')

function checkAuthorization(message, key) {
    console.log(`[TRACE] auth.checkAuthorization(Message@${message.id}, ${JSON.stringify(key)})`)
    return (
        // guild owner can always perform any action
        message.member.id == message.guild.owner.id
        // bot admin can always perform any action (for debugging and support)
        || message.member.id == config.admin
    )
}

module.exports = { checkAuthorization }
