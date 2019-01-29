const handlers = []

function registerPresenceUpdateHandler(handler) {
    console.log(`[TRACE] presenceUpdateProcessor.registerPresenceUpdateHandler(handler)`)
    handlers.push(handler)
}

function onPresenceUpdate(oldMember, newMember) {
    // only process relevant presence updates
    if (isRelevantUpdate(oldMember, newMember)) {
        console.log(`[TRACE] presenceUpdateProcessor.onPresenceUpdate(GuildMember@${oldMember.id}, GuildMember@${newMember.id})`)
        // call all registered handlers
        handlers.forEach(handler => handler(oldMember, newMember))
    }
}

function isRelevantUpdate(oldMember, newMember) {
    return (
        // ignore all bots
        !oldMember.user.bot && !newMember.user.bot
        // listen for game activity changes
        && (oldMember.presence.game ? oldMember.presence.game.applicationId : false) !== (newMember.presence.game ? newMember.presence.game.applicationId : false)
    )
}

module.exports = { registerPresenceUpdateHandler, onPresenceUpdate }
