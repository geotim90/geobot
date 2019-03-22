const {log} = require("./log.js");

// initialize Enmap database
const Enmap = require("enmap");
const db = new Enmap({
    name: "geobot",
    fetchAll: false,
    autoFetch: true,
    cloneLevel: "deep"
});

function db_get(guildId, path) {
    return db.get(guildId, path)
}

function db_set(guildId, value, path) {
    log("data", `guild=${guildId}`, `SET ${path} = ${JSON.stringify(value)}`);
    return db.set(guildId, value, path)
}

function db_delete(guildId, path = null) {
    if (path === null) {
        log("data", `guild=${guildId}`, `DELETE`);
        return db.delete(guildId)
    } else {
        log("data", `guild=${guildId}`, `DELETE ${path}`);
        return db.delete(guildId, path)
    }
}

function db_has(guildId, path) {
    return db.has(guildId, path)
}

function db_push(guildId, value, path) {
    log("data", `guild=${guildId}`, `PUSH ${path} += ${JSON.stringify(value)}`);
    return db.push(guildId, value, path)
}

function db_remove(guildId, value, path) {
    log("data", `guild=${guildId}`, `REMOVE ${path} -= ${JSON.stringify(value)}`);
    return db.remove(guildId, value, path)
}

module.exports = {
    db_get,
    db_set,
    db_delete,
    db_has,
    db_push,
    db_remove
};
