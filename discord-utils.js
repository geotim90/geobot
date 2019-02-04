const games = require('./games.json')

function findGame(msg, input) {
    console.log(`[TRACE] discord-utils.findGame(Message@${msg.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // game application id
            findGameByApplicationId(msg, input)
                .then(game => resolve(game))
                .catch(error => reject(error))
        } else {
            // game name?
            const search = input.toLowerCase()
            const member = msg.guild.members.find(e =>
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

function findMember(msg, input) {
    console.log(`[TRACE] discord-utils.findMember(Message@${msg.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // member id
            findMemberById(msg, input)
                .then(member => resolve(member))
                .catch(error => reject(error))
        } else if (input.match(/^<@\d+>$/)) {
            // member mention
            findMemberById(msg, input.slice(2, -1))
                .then(member => resolve(member))
                .catch(error => reject(error))
        } else {
            // member name?
            const search = input.toLowerCase()
            const member = msg.guild.members.find(e =>
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

function findMemberById(msg, memberId) {
    console.log(`[TRACE] discord-utils.findMemberById(Message@${msg.id}, ${JSON.stringify(memberId)})`)
    return new Promise((resolve, reject) => {
        if (!memberId || !memberId.match(/^\d+$/)) {
            reject({ message: 'Invalid member id' })
        } else {
            // guild member?
            const member = msg.guild.members.get(memberId)
            if (member) {
                resolve(member.user)
            } else {
                msg.guild.fetchMember(memberId)
                    .then(member => resolve(member))
                    .catch(error => reject(error))
            }
        }
    })
}

function findRole(msg, input) {
    console.log(`[TRACE] discord-utils.findRole(Message@${msg.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // role id
            return findRoleById(msg, input)
                .then(role => resolve(role))
                .catch(error => reject(error))
        } else if (input.match(/^<@&\d+>$/)) {
            // role mention
            return findRoleById(msg, input.slice(3, -1))
                .then(role => resolve(role))
                .catch(error => reject(error))
        } else {
            // role name?
            const search = input.toLowerCase()
            const role = msg.guild.roles.find(e =>
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

function findRoleById(msg, roleId) {
    console.log(`[TRACE] discord-utils.findRoleById(Message@${msg.id}, ${JSON.stringify(roleId)})`)
    return new Promise((resolve, reject) => {
        if (!roleId || !roleId.match(/^\d+$/)) {
            reject({ message: 'Invalid role id' })
        } else {
            const role = msg.guild.roles.get(roleId)
            if (role) {
                resolve(role)
            } else {
                reject({ message: 'No matching role found' })
            }
        }
    })
}

function findUser(msg, input) {
    console.log(`[TRACE] discord-utils.findUser(Message@${msg.id}, ${JSON.stringify(input)})`)
    return new Promise((resolve, reject) => {
        if (!input) {
            reject({ message: 'Missing argument' })
        } else if (input.match(/^\d+$/)) {
            // user id
            findUserById(msg, input)
                .then(user => resolve(user))
                .catch(error => reject(error))
        } else if (input.match(/^<@\d+>$/)) {
            // user mention
            findUserById(msg, input.slice(2, -1))
                .then(user => resolve(user))
                .catch(error => reject(error))
        } else {
            // user name?
            const search = input.toLowerCase()
            const member = msg.guild.members.find(e =>
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

function findUserById(msg, userId) {
    console.log(`[TRACE] discord-utils.findUserById(Message@${msg.id}, ${JSON.stringify(userId)})`)
    return new Promise((resolve, reject) => {
        if (!userId || !userId.match(/^\d+$/)) {
            reject({ message: 'Invalid user id' })
        } else {
            // guild member?
            const member = msg.guild.members.get(userId)
            if (member) {
                resolve(member.user)
            } else {
                // client user?
                msg.client.fetchUser(userId, false)
                    .then(user => resolve(user))
                    .catch(error => reject(error))
            }
        }
    })
}

module.exports = { findGame, findMember, findRole, findUser }
