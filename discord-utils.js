const games = require('./games.json')

function findGame(message, input) {
    console.log(`[TRACE] discord-utils.findGame(Message@${message.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // game application id
            findGameByApplicationId(message, input)
                .then(game => resolve(game))
                .catch(error => reject(error))
        } else {
            // game name?
            const search = input.toLowerCase()
            const member = message.guild.members.find(e =>
                e.presence && e.presence.game && e.presence.game.name && e.presence.game.name.toLowerCase().includes(search)
            )
            if (member) {
                resolve(member.presence.game)
            } else {
                let game = false
                for (const applicationId in games) {
                    if (games[applicationId].name && games[applicationId].name.toLowerCase().includes(search)) {
                        game = games[applicationId]
                        break
                    }
                }
                if (game) {
                    resolve(game)
                } else {
                    reject({ message: 'No matching game found' })
                }
            }
        }
    })
}

function findMember(message, input) {
    console.log(`[TRACE] discord-utils.findMember(Message@${message.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // member id
            findMemberById(message, input)
                .then(member => resolve(member))
                .catch(error => reject(error))
        } else if (input.match(/^<@\d+>$/)) {
            // member mention
            findMemberById(message, input.slice(2, -1))
                .then(member => resolve(member))
                .catch(error => reject(error))
        } else {
            // member name?
            const search = input.toLowerCase()
            const member = message.guild.members.find(e =>
                (e.nickname && e.nickname.toLowerCase().includes(search))
                || (e.user.tag && e.user.tag.toLowerCase().includes(search))
                || (e.user.username && e.user.username.toLowerCase().includes(search))
            )
            if (member) {
                resolve(member)
            } else {
                reject({ message: 'No matching member found' })
            }
        }
    })
}

function findMemberById(message, memberId) {
    console.log(`[TRACE] discord-utils.findMemberById(Message@${message.id}, ${JSON.stringify(memberId)})`)
    return new Promise((resolve, reject) => {
        if (!memberId || !memberId.match(/^\d+$/)) {
            reject({ message: 'Invalid member id' })
        } else {
            // guild member?
            const member = message.guild.members.get(memberId)
            if (member) {
                resolve(member.user)
            } else {
                message.guild.fetchMember(memberId)
                    .then(member => resolve(member))
                    .catch(error => reject(error))
            }
        }
    })
}

function findRole(message, input) {
    console.log(`[TRACE] discord-utils.findRole(Message@${message.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // role id
            return findRoleById(message, input)
                .then(role => resolve(role))
                .catch(error => reject(error))
        } else if (input.match(/^<@&\d+>$/)) {
            // role mention
            return findRoleById(message, input.slice(3, -1))
                .then(role => resolve(role))
                .catch(error => reject(error))
        } else {
            // role name?
            const search = input.toLowerCase()
            const role = message.guild.roles.find(e =>
                e.name && e.name.toLowerCase().includes(search)
            )
            if (role) {
                resolve(role)
            } else {
                reject({ message: 'No matching role found' })
            }
        }
    })
}

function findRoleById(message, roleId) {
    console.log(`[TRACE] discord-utils.findRoleById(Message@${message.id}, ${JSON.stringify(roleId)})`)
    return new Promise((resolve, reject) => {
        if (!roleId || !roleId.match(/^\d+$/)) {
            reject({ message: 'Invalid role id' })
        } else {
            const role = message.guild.roles.get(roleId)
            if (role) {
                resolve(role)
            } else {
                reject({ message: 'No matching role found' })
            }
        }
    })
}

function findUser(message, input) {
    console.log(`[TRACE] discord-utils.findUser(Message@${message.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // user id
            findUserById(message, input)
                .then(user => resolve(user))
                .catch(error => reject(error))
        } else if (input.match(/^<@\d+>$/)) {
            // user mention
            findUserById(message, input.slice(2, -1))
                .then(user => resolve(user))
                .catch(error => reject(error))
        } else {
            // user name?
            const search = input.toLowerCase()
            const member = message.guild.members.find(e =>
                (e.nickname && e.nickname.toLowerCase().includes(search))
                || (e.user.tag && e.user.tag.toLowerCase().includes(search))
                || (e.user.username && e.user.username.toLowerCase().includes(search))
            )
            if (member) {
                resolve(member.user)
            } else {
                reject({ message: 'No matching member found' })
            }
        }
    })
}

function findUserById(message, userId) {
    console.log(`[TRACE] discord-utils.findUserById(Message@${message.id}, ${JSON.stringify(userId)})`)
    return new Promise((resolve, reject) => {
        if (!userId || !userId.match(/^\d+$/)) {
            reject({ message: 'Invalid user id' })
        } else {
            // guild member?
            const member = message.guild.members.get(userId)
            if (member) {
                resolve(member.user)
            } else {
                // client user?
                message.client.fetchUser(userId, false)
                    .then(user => resolve(user))
                    .catch(error => reject(error))
            }
        }
    })
}

module.exports = { findGame, findMember, findRole, findUser }
