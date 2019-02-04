const Discord = require('discord.js')
const { inspect } = require('util')

function abbreviate(string, characterLimit) {
    if (characterLimit && string.length > characterLimit) {
        return string.substring(0, characterLimit - 4).trim() + ' ...'
    } else {
        return string
    }
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp)
    return date.toISOString()
}

function stringify(obj, characterLimit = false) {
    return abbreviate(Discord.escapeMarkdown(inspect(obj), true), characterLimit)
}

module.exports = { abbreviate, formatTimestamp, stringify }
