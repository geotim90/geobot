# Geobot
Guild activity monitor
(currently in development with limited up-time)

## Invite
https://discordapp.com/oauth2/authorize?client_id=536808091455324160&scope=bot&permissions=2048

## Permissions
*Geobot* only needs to be able to send messages to work.

## Commands
*Geobot* will only respond to messages that start with a mention for it, e.g. `@Geobot`.

### `ping`

### `inspect`

## Development

### Installing dependencies

* https://discordjs.guide/preparations/
* https://enmap.evie.codes/install

### Configuration

#### `config.json`

```json
{
    "admin": "replace-with-your-discord-user-id",
    "token": "replace-with-your-discord-bot-token"
}
```

### Launch

```sh
node .
```

### Database

```js
{
    "games": {
        "${applicationID}": "${name}"
    },
    
    "${guild.id}": {
        "${member.id}": {
            "lastMessage": "${timestamp}",
            "lastOnline": "${timestamp}",
            "${applicationID}": "${timestamp}"
        }
    }
}
```