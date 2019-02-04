const { checkAuthorization } = require('./auth')
const { findGame, findMember, findRole, findUser } = require('./discord-utils')
const { formatTimestamp, stringify } = require('./format')
const { reply } = require('./messaging')

function installInspect(handlers) {
    handlers['i'] = handleInspectMessage
    handlers['inspect'] = handleInspectMessage
}

function handleInspectMessage(message) {
    console.log(`[TRACE] inspect.handleInspectMessage(Message@${message.id})`)
    if (checkAuthorization(message, 'inspect')) {
        if (message.args[2]) {
            if (message.args[2].match(/^u(ser)?$/i)) {
                inspectUser(message, message.args[3])
            } else if (message.args[2].match(/^r(ole)?$/i)) {
                inspectRole(message, message.args[3])
            } else if (message.args[2].match(/^g(ame)?$/i)) {
                inspectGame(message, message.args[3])
            } else if (message.args[2].match(/^(d(ata(base)?)?|db)$/i)) {
                inspectDatabase(message)
            } else {
                inspectUser(message, message.args[2])
            }
        } else {
            reply(message, 'here are some examples on how to use `inspect`:```java\n'
                + `@Geobot inspect user ${message.client.user.id}\n`
                + `@Geobot inspect role ${message.guild.defaultRole.id}\n`
                + '@Geobot inspect game 422772752647323649\n'
                + '```')
        }
    }
}

function inspectDatabase(message) {
    console.log(`[TRACE] inspect.inspectDatabase(Message@${message.id})`)
    reply(message, 'here is what I found:```js\n' + stringify(message.client.db.get(message.guild.id), 1945) + '```')
}

function inspectGame(message, input) {
    console.log(`[TRACE] inspect.inspectGame(Message@${message.id}, ${JSON.stringify(input)})`)
    findGame(message, input).then(game => {
        reply(message, 'here is what I found:```js\n' + stringify(getGameProps(game), 1945) + '```')
    }).catch(noGame => {
        console.log(`[DEBUG] Could not find game ${JSON.stringify(input)} - reason: ${JSON.stringify(noGame.message)}`)
        reply(message, `no game ${JSON.stringify(input)} found`)
    })
}

function inspectRole(message, input) {
    console.log(`[TRACE] inspect.inspectRole(Message@${message.id}, ${JSON.stringify(input)})`)
    findRole(message, input).then(role => {
        reply(message, 'here is what I found:```js\n' + stringify(getRoleProps(role), 1945) + '```')
    }).catch(noRole => {
        console.log(`[DEBUG] Could not find role ${JSON.stringify(input)} - reason: ${JSON.stringify(noRole.message)}`)
        reply(message, `no role ${JSON.stringify(input)} found`)
    })
}

function inspectUser(message, input) {
    console.log(`[TRACE] inspect.inspectUser(Message@${message.id}, ${JSON.stringify(input)})`)
    findMember(message, input).then(member => {
        reply(message, 'here is what I found:```js\n' + stringify(getMemberProps(member), 1945) + '```')
    }).catch(noMember => {
        console.log(`[DEBUG] Could not find member ${JSON.stringify(input)} - reason: ${JSON.stringify(noMember.message)}`)
        findUser(message, input).then(user => {
            reply(message, 'here is what I found:```js\n' + stringify(getUserProps(user), 1945) + '```')
        }).catch(noUser => {
            console.log(`[DEBUG] Could not find user ${JSON.stringify(input)} - reason: ${JSON.stringify(noUser.message)}`)
            reply(message, `no user ${JSON.stringify(input)} found`)
        })
    })
}

function getGameProps(game) {
    const props = {
        name: game.name,
        applicationID: game.applicationID
    }
    return props
}

function getMemberProps(member) {
    const props = { ...member }
    props.guild = member.guild.id
    props.joinedTimestamp = formatTimestamp(member.joinedTimestamp)
    return props
}

function getRoleProps(role) {
    const props = { ...role }
    props.guild = role.guild.id
    return props
}

function getUserProps(user) {
    const props = { ...user }
    return props
}

module.exports = { installInspect }
