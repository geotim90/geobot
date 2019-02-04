const { checkAuthorization } = require('./auth')
const { findGame, findMember, findRole, findUser } = require('./discord-utils')
const { formatTimestamp, stringify } = require('./format')
const { reply } = require('./messaging')

function installInspect(handlers) {
    handlers['i'] = handleInspectMessage
    handlers['inspect'] = handleInspectMessage
}

function handleInspectMessage(msg) {
    console.log(`[TRACE] inspect.handleInspectMessage(Message@${msg.id})`)
    if (checkAuthorization(msg, 'inspect')) {
        if (msg.args[2]) {
            if (msg.args[2].match(/^u(ser)?$/i)) {
                inspectUser(msg, msg.args[3])
            } else if (msg.args[2].match(/^r(ole)?$/i)) {
                inspectRole(msg, msg.args[3])
            } else if (msg.args[2].match(/^g(ame)?$/i)) {
                inspectGame(msg, msg.args[3])
            } else if (msg.args[2].match(/^(d(ata(base)?)?|db)$/i)) {
                inspectDatabase(msg)
            } else {
                inspectUser(msg, msg.args[2])
            }
        } else {
            reply(msg, 'here are some examples on how to use `inspect`:```' + `
                @Geobot inspect user ${msg.client.user.id}\n`
                + `@Geobot inspect role ${msg.guild.defaultRole.id}\n`
                + '@Geobot inspect game 422772752647323649\n'
                + '```')
        }
    }
}

function inspectDatabase(msg) {
    console.log(`[TRACE] inspect.inspectDatabase(Message@${msg.id})`)
    reply(msg, 'here is what I found:```js\n' + stringify(msg.client.db.get(msg.guild.id), 1945) + '```')
}

function inspectGame(msg, input) {
    console.log(`[TRACE] inspect.inspectGame(Message@${msg.id}, ${JSON.stringify(input)})`)
    findGame(msg, input).then(game => {
        reply(msg, 'here is what I found:```js\n' + stringify(getGameProps(game), 1945) + '```')
    }).catch(noGame => {
        console.log(`[DEBUG] Could not find game ${JSON.stringify(input)} - reason: ${JSON.stringify(noGame.message)}`)
        reply(msg, `no game ${JSON.stringify(input)} found`)
    })
}

function inspectRole(msg, input) {
    console.log(`[TRACE] inspect.inspectRole(Message@${msg.id}, ${JSON.stringify(input)})`)
    findRole(msg, input).then(role => {
        reply(msg, 'here is what I found:```js\n' + stringify(getRoleProps(role), 1945) + '```')
    }).catch(noRole => {
        console.log(`[DEBUG] Could not find role ${JSON.stringify(input)} - reason: ${JSON.stringify(noRole.message)}`)
        reply(msg, `no role ${JSON.stringify(input)} found`)
    })
}

function inspectUser(msg, input) {
    console.log(`[TRACE] inspect.inspectUser(Message@${msg.id}, ${JSON.stringify(input)})`)
    findMember(msg, input).then(member => {
        reply(msg, 'here is what I found:```js\n' + stringify(getMemberProps(member), 1945) + '```')
    }).catch(noMember => {
        console.log(`[DEBUG] Could not find member ${JSON.stringify(input)} - reason: ${JSON.stringify(noMember.message)}`)
        findUser(msg, input).then(user => {
            reply(msg, 'here is what I found:```js\n' + stringify(getUserProps(user), 1945) + '```')
        }).catch(noUser => {
            console.log(`[DEBUG] Could not find user ${JSON.stringify(input)} - reason: ${JSON.stringify(noUser.message)}`)
            reply(msg, `no user ${JSON.stringify(input)} found`)
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
