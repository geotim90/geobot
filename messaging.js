function reply(message, text) {
    console.log(`[TRACE] messaging.reply(Message@${message.id}, ${JSON.stringify(text)})`)
    message.reply(text)
        .then(out => console.log(`[MESSAGE] Replied to Message@${message.id} with Message@${out.id}(${JSON.stringify(out.content)})`))
        .catch(error => console.log(`[ERROR] Failed to reply to Message@${message.id} with ${JSON.stringify(text)} - reason: ${JSON.stringify(error.message)}`))
}

function respond(message, text) {
    console.log(`[TRACE] messaging.send(Message@${message.id}, ${JSON.stringify(text)})`)
    message.channel.send(text)
        .then(out => console.log(`[MESSAGE] Responded to Message@${message.id} with Message@${out.id}(${JSON.stringify(out.content)})`))
        .catch(error => console.log(`[ERROR] Failed to respond to Message@${message.id} with ${JSON.stringify(text)} - reason: ${JSON.stringify(error.message)}`))
}

module.exports = { reply, respond }
