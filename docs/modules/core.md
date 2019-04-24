# Module: `core`

These commands are a core part of Geobot and are always available.
In other words, this module is active by default and cannot be deactivated.

* [`help`](#command-help)
* [`modules`](#command-modules)
* [`permissions`](#command-permissions)
* [`ping`](#command-ping)
* [`prefix`](#command-prefix)
* [`resolve`](#command-resolve)

## Command: `help`

Use the **help** command to find out which commands you can use and what they do.

Command                                | Permission node      | Description
---------------------------------------|----------------------|------------
**help**                               | `core.help`          | Displays a list of commands available to the current user. The list is limited by the permissions of the current user in the current channel.
**help** \<*command*\> \[*arguments*\] | depends on *command* | Displays a description of the *command* and how to use it if the current user has the approriate permissions in the current channel. *Arguments* may be provided to get more details on specific features.

Option                  | Description
------------------------|------------
--all                   | Lists all available commands regardless of the current scope.
--channel \<*channel*\> | Executes the command within the scope of the provided *channel*.

## Command: `modules`

Use the **modules** command to activate or deactivate Geobot modules on your server.

Command                         | Permission node       | Description
--------------------------------|-----------------------|------------
**modules** (get)               | `core.modules.get`    | Lists all modules and whether they are active for the current server.
**modules** add \<*module*\>    | `core.modules.add`    | Activates a *module* for the current server.
**modules** remove \<*module*\> | `core.modules.remove` | Disables a *module* for the current server.

**Alternative syntax**
* You can also use `activate`, `enable` or `install` instead of `add`.
* You can also use `deactivate`, `disable` or `uninstall` instead of `remove`.

## Command: `permissions`

Use the **permissions** command to configure which users are allowed to execute which commands in which channels.

Server administrators are not restricted by permissions.
Permissions are denied to everyone else by default.
Configured permissions are applied in the following order.
Each step overrides any previous permissions when calculating the effective permissions.

Order | Permission type          | Options
------|--------------------------|--------
1.    | Server permissions       | `--server`
2.    | Role permissions         | `--role <role>`
3.    | User permissions         | `--user <user>`
4.    | Channel permissions      | `--channel <channel>`
5.    | Channel role permissions | `--channel <channel> --role <role>`
6.    | Channel user permissions | `--channel <channel> --user <user>`

Users cannot configure permissions for roles above their own or grant permissions for nodes they do not have access to themselves.
Users cannot configure permissions for users with roles above their own or grant permissions for nodes they do not have access to themselves.

Command                                        | Permission node          | Description
-----------------------------------------------|--------------------------|------------
**permissions** (get) \<*target*\>             | `core.permissions.get`   | Lists the effective permissions for the provided *target* (see options below). 
**permissions** debug \<*target*\>             | `core.permissions.debug` | Lists all relevant permission node configurations for the provided *target* (see options below).
**permissions** allow \<*nodes*\> \<*target*\> | `core.permissions.allow` | Grants the provided permission *nodes* to the provided *target* (see options below).
**permissions** deny \<*nodes*\> \<*target*\>  | `core.permissions.deny`  | Refuses the provided permission *nodes* to the provided *target* (see options below).
**permissions** reset \<*nodes*\> \<*target*\> | `core.permissions.reset` | Resets the provided permission *nodes* for the provided *target* (see options below).

Option                  | Description
------------------------|------------
--all                   | Short-hand for all permission nodes.
--channel \[*channel*\] | Used to get or set *channel* permissions.
--node \<*nodes*\>      | Permission *nodes* may be provided to check specific permissions.
--server                | Used to get or set server permissions.
--role \<*role*\>       | Used to get or set *role* permissions.
--user \[*user*\]       | Used to get or set *user* permissions.

**Alternative syntax**
* You can also use `add` or `grant` instead of `allow`.
* You can also use `block` or `refuse` instead of `deny`.
* You can also use `clear`, `default` or `remove` instead of `reset`.

## Command: `ping`

Use the **ping** command to measure the latency between Discord and Geobot.

Command  | Permission node | Description
---------|-----------------|------------
**ping** | `core.ping`     | Displays the latency between Discord and Geobot.

## Command: `prefix`

Use the **prefix** command to change the Geobot prefix on your server.

The default prefix is `G>`.
Regardless of the currently set prefix, commands can always be executed by mentioning the bot directly (e.g. `@Geobot`).

Command                     | Permission node   | Description
----------------------------|-------------------|------------
**prefix** (get)            | `core.prefix.get` | Displays the currently set prefix.
**prefix** set \<*prefix*\> | `core.prefix.set` | Changes the prefix to the specified *prefix*.

## Command: `resolve`

Use the **resolve** command to find IDs, mentions and names for various types of references.

Command                     | Permission node | Description
----------------------------|-----------------|------------
**resolve** \<*reference*\> | `core.resolve`  | Attempts to interpret the provided *reference* and displays some basic information related to it.

Option                    | Description
--------------------------|------------
--channel \<*reference*\> | Attempts to interpret the provided *reference* as a channel.
--game \<*reference*\>    | Attempts to interpret the provided *reference* as a game.
--node \<*reference*\>    | Attempts to interpret the provided *reference* as a permission node.
--role \<*reference*\>    | Attempts to interpret the provided *reference* as a role.
--user \<*reference*\>    | Attempts to interpret the provided *reference* as a user.
