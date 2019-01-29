# Geobot

Geobot is an open-source multi-purpose Discord bot.
Its primary instance is hosted by [Geotim](https://github.com/geotim90), who is also the owner of this repository.
Feel free to make suggestions in the [Issues](https://github.com/geotim90/geobot/issues) section or even submit you own pull requests.
The code is licensed under [Apache License Version 2.0](#license).

## Invite

You can add Geobot to your Discord server by inviting the bot to your server using the link below.
You need to be the owner of the Discord server or have an "Administrator" or "Manage Server" role in order to do this.
You can grant or restrict permissions for Geobot as you wish.
Geobot will let you know if any required permissions are missing and default to direct messages if everything else fails.

https://discordapp.com/oauth2/authorize?client_id=536808091455324160&scope=bot&permissions=519232

## Commands

To execute commands, write a message that begins with `G>` or `@Geobot` in a channel Geobot can read messages.
You can change the `G>` prefix using the `prefix set` command.
The `@Geobot` mention can be changed by setting a nickname for the bot on your server.

## Modules

Geobot has multiple modules, which can be activated seperately.
By default, only the `core` module is active.
Other modules can be activated with the `module add` command.
Each module has its own documentation in the docs folder of the repository.
Access to individual commands can be managed via permissions.

## Permissions

Geobot uses its own permission system that is based on how the Discord permission system works.
For more details, check the core module documentation in the docs folder.
Server administrators are not restricted by permissions.

## Support

If you need any help, come over to [Geozone](https://discord.gg/jKVZFhD) and feel free to ask any questions in the support channel there.

## License

Copyright &copy; 2019 [Geotim](https://github.com/geotim90)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use files in this repository except in
compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
