function log(type, context, message, obj) {
    if (context) {
        console.log(`${new Date().toISOString()} [${type.toUpperCase()}] {${context}} ${message}`, obj ? obj : "")
    } else {
        console.log(`${new Date().toISOString()} [${type.toUpperCase()}] ${message}`, obj ? obj : "")
    }
}

module.exports = {
    log
};
