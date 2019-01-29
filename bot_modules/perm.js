function install(registerMessageHandler) {
    console.log('[TRACE] perm.install(registerMessageHandler)')
    registerMessageHandler('p', handleMessage)
    registerMessageHandler('perm', handleMessage)
    registerMessageHandler('perms', handleMessage)
    registerMessageHandler('permission', handleMessage)
    registerMessageHandler('permissions', handleMessage)
}

function handleMessage(msg) {
    console.log(`[TRACE] perm.handleMessage(Message@${msg.id})`)
}

module.exports = { install }
