# zAuctionHouse-Discord
Discord bot for zAuctionHouse

# How to configure the plugin

1. You must create a new <a href="https://discord.com/developers/applications">application</a> on the develop discord panel.
2. Check that the bot has the permissions below:
![Discord screen](https://img.groupez.xyz/zauctionhouse/v3/discord/discord.png)
3. Download the latest version of the plugin.
4. Add the plugin to the ``plugins`` folder on your server.
5. Start your server a first time to generate the configuration file and shut down your server.
6. Configure the ``config.json`` file by adding the ``token`` of your bot, the ``channel ID`` to send the messages and if needed the ``gatewayIntents``.
7. You can now restart your server and use the plugin.

## Variables

* ``%seller%`` - Get seller name
* ``%buyer%`` - Get buyer name, only for edit message
* ``%price%`` - Get price
* ``%currency%`` - Get currency
* ``%amount%`` - Get amount of item's
* ``%material%`` - Get material name (or replace for your language with <a href="https://groupez.dev/resources/ztranslator.230">zTranslator</a>)
* ``%enchant%`` - Get enchant list
