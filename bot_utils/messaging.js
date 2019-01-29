function react(msg, emoji) {
    console.log(`[TRACE] messaging.react(Message@${msg.id}, ${JSON.stringify(emoji)})`)
    msg.react(emoji)
        .then(() => console.log(`[INFO] Reacted to Message@${msg.id} with ${JSON.stringify(emoji)}`))
        .catch(error => console.log(`[ERROR] Failed to react to Message@${msg.id} with ${JSON.stringify(emoji)} - reason: ${JSON.stringify(error.message)}`))
}

function reply(msg, text) {
    console.log(`[TRACE] messaging.reply(Message@${msg.id}, ${JSON.stringify(text)})`)
    msg.reply(text)
        .then(out => console.log(`[INFO] Replied to Message@${msg.id} with Message@${out.id}(${JSON.stringify(out.content)})`))
        .catch(error => console.log(`[ERROR] Failed to reply to Message@${msg.id} with ${JSON.stringify(text)} - reason: ${JSON.stringify(error.message)}`))
}

function respond(msg, text) {
    console.log(`[TRACE] messaging.send(Message@${msg.id}, ${JSON.stringify(text)})`)
    msg.channel.send(text)
        .then(out => console.log(`[INFO] Responded to Message@${msg.id} with Message@${out.id}(${JSON.stringify(out.content)})`))
        .catch(error => console.log(`[ERROR] Failed to respond to Message@${msg.id} with ${JSON.stringify(text)} - reason: ${JSON.stringify(error.message)}`))
}

module.exports = { react, reply, respond }
