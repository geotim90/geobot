# Geobot
Guild activity monitor

## Invite
https://discordapp.com/oauth2/authorize?client_id=536808091455324160&scope=bot&permissions=2048

## Permissions
*Geobot* needs to be able to **Read Messages** in channels where guild members will be posting messages. It needs to be able to **Send Messages** in channels where bot commands will be executed.

## Commands
*Geobot* will only respond to messages that start with a mention for it, e.g. `@Geobot help`. Individual commands are explained in the following sections.

### Reports

*Mods* and *admins* can execute `@Geobot report` to get a report on (1) *initiates* that have failed to make a *contribution* within a certain number of days after becoming an *initiate*, (2) *initiates* that have made a *contribution* within a certain number of days after becoming an *initiate* and (3) *members* that are *inactive*.

*Mods* and *admins* can execute `@Geobot report $1` to get a detailed report on a particular guild member. `$1` may be a user mention, user name or user ID.

### Roles

*Geobot* uses **Roles** to determine which users should be monitored and which users can execute which commands.

There are four permission levels. If a user has no matching roles, *Geobot* will not respond to any commands from them and will also not include them in any reports.

#### Initiate

*Initiates* are expected to make a *contribution* within a certain number of days after becoming an *initiate*.

*Mods* and *admins* can execute `@Geobot set contribution $1` to confirm that a guild member has made a *contribution*. *Admins* can execute `@Geobot unset contribution $1` to undo the aforementioned command. *Mods* and *admins* can execute `@Geobot get contribution $1` to check whether a guild member has made a *contribution*. `$1` may be a user mention, user name or user ID.

*Admins* can execute `@Geobot add role initiate $1` and `@Geobot remove role initiate $1` to manage which guild members are considered *initiates* based on their roles. *Admins* can execute `@Geobot get role initiate` to check which roles are configured. `$1` may be a role mention, role name or role ID.

*Admins* can execute `@Geobot set timeout contribution $1` to configure how many days *initiates* have to *contribute*. *Mods* and *admins* can execute `@Geobot get timeout contribution` to check how this setting is currently configured. `$1` must be a positive integer between `1` (1 day) and `365` (1 year). The default value is `14` (2 weeks).

*Admins* can execute `@Geobot set member joined $1 $2` to overwite when a guild member became an *initiate*. *Mods* and *admins* can execute `@Geobot get member joined $1` to check when a guild member became an *initiate*. `$1` may be a user mention, user name or user ID. `$2` must be an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`).

#### Member

*Members* are expected to be *active* within a certain number of days past. Activites include (1) being online on Discord, (2) posting messages in channels *Geobot* can read messages in and (3) playing certain games.

*Admins* can execute `@Geobot add role member $1` and `@Geobot remove role member $1` to manage which guild members are considered *members* based on their roles. *Admins* can execute `@Geobot get role member` to check which roles are configured. `$1` may be a role mention, role name or role ID.

*Admins* can execute `@Geobot set timeout lastOnline $1` to configure how many days ago *members* need to have been online on Discord. *Mods* and *admins* can execute `@Geobot get timeout lastOnline` to check how this setting is currently configured. `$1` must be a positive integer between `1` (1 day) and `365` (1 year). The default value is `60` (2 months).

*Admins* can execute `@Geobot set timeout lastMessage $1` to configure how many days ago *members* need to have posted messages in channels *Geobot* can read messages in.  *Mods* and *admins* can execute `@Geobot get timeout lastMessage` to check how this setting is currently configured. `$1` must be a positive integer between `1` (1 day) and `365` (1 year). The default value is `60` (2 months).

*Admins* can execute `@Geobot set game timeout $1 $2` to configure how many days ago *members* need to have played a certain game. *Admins* can execute `@Geobot unset game timeout $1` to remove a game from the configuration. *Mods* and *admins* can execute `@Geobot get game timeout $1` to check how this setting is currently configured. `$1` may a game name or application ID. `$2` must be a positive integer between `1` (1 day) and `365` (1 year). No games are monitored by default.

*Admins* can execute `@Geobot set member lastOnline $1 $2` to overwite when a guild member was last online. *Mods* and *admins* can execute `@Geobot get member lastOnline $1` to check when a guild member was last online. `$1` may be a user mention, user name or user ID. `$2` must be an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`).

*Admins* can execute `@Geobot set member lastMessage $1 $2` to overwite when a guild member last posted a message. *Mods* and *admins* can execute `@Geobot get member lastMessage $1` to check when a guild member last posted a message. `$1` may be a user mention, user name or user ID. `$2` must be an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`).

*Admins* can execute `@Geobot set member lastPlayed $1 $2 $3` to overwite when a guild member last played a certain game. *Mods* and *admins* can execute `@Geobot get member lastPlayed $1 $2` to check when a guild member last played a certain game. `$1` may be a user mention, user name or user ID. `$2` may a game name or application ID. `$3` must be an ISO 8601 compliant UTC date or timestamp (e.g. `1999-12-31` or `1999-12-31T23:59:59.999Z`).

#### Mod

*Mods* can execute *reports* and confirm when a guild member has made a *contribution*.

*Admins* can execute `@Geobot add role mod $1` and `@Geobot remove role mod $1` to manage which guild members are considered *mods* based on their roles. *Admins* can execute `@Geobot get role mod` to check which roles are configured. `$1` may be a role mention, role name or role ID.

#### Admin

*Admins* can execute all bot commands. The guild owner will __always__ be an *admin*, as well as the bot superadmin (*Geotim* if using the invite link above).

*Admins* can execute `@Geobot add role admin $1` and `@Geobot remove role admin $1` to manage which guild members are considered *admins* based on their roles. *Admins* can execute `@Geobot get role admin` to check which roles are configured. `$1` may be a role mention, role name or role ID.

## Development

The following sections are only relevant for developers of the bot itself.

### Installing dependencies

* https://discordjs.guide/preparations/
* https://enmap.evie.codes/install

### Configuration

#### `config.json`

```json
{
    "admin": "replace-with-your-discord-user-id",
    "devmode": true,
    "token": "replace-with-your-discord-bot-token"
}
```

### Database

```js
enmap = {
    "games": {
        "${applicationID}": "${name}",
        ...
    },
    
    "${guild.id}": {
        "roles": {
            "initiate": ["${role.id}", ...],
            "member": ["${role.id}", ...],
            "mod": ["${role.id}", ...],
            "admin": ["${role.id}", ...]
        },
        "timeouts": {
            "contribution": 14,
            "lastOnline": 60,
            "lastMessage": 60,
            "${applicationID}": 60,
            ...
        },
        "members": {
            "${member.id}": {
                "joined": "${timestamp}",
                "contribution": false,
                "lastOnline": "${timestamp}",
                "lastMessage": "${timestamp}",
                "${applicationID}": "${timestamp}",
                ...,
                "notes": [
                	{
                	    "author": "${message.author.id}",
                	    "scope": "${(private|public|shared)}",
                	    "message": "${message.id}",
                	    "timestamp": "${message.createdTimestamp}",
                	    "content": "..."
	                },
	                ...
                ]
            },
            ...
        }
    },
    ...
}
```
