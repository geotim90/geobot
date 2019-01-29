# Geobot modules

This folder contains the documentation for each Geobot module.

## Commands

Each module comes with its own set of commands.
The description of each command will employ the following formatting.

Format         | Description 
---------------|------------
**command**    | Identifies which command is executed and must be provided exactly as shown.
keyword        | Commonly defines what action the command should execute and must be provided exactly as shown.
(keyword)      | Optional keywords can be omitted for brevity.<br>Do not type the parentheses ( ).
\<*argument*\> | Arguments refer to what kind of value should be provided, for example a *user* reference.<br>Do not type the angles \< \>.
\[*argument*\] | Optional arguments can be omitted, but may cause the command to behave differently.<br>Do not type the brackets \[ \].
A&vert;B       | Syntax separated by pipes &vert; are alternatives.<br>Do not type the pipe &vert;.

## Options

Options are arguments that are denoted by flags such as `--user` or `-u`.
They typically provide the scope for a command or action by providing one of the following types of reference.
If the provided reference is ambiguous, Geobot will prompt the user to use a unique reference.
Generally, it is advised to use IDs or mentions whenever possible.

The following table describes some common options used in commands.
Please refer to the documentation of each individual command to see which options are supported and what effect they have.

Option                    | Description
--------------------------|------------
(--channel) \[*channel*\] | A channel ID, mention or name.<br>If no *channel* is provided, this will refer to the current channel.<br>The `--channel` flag is optional if a channel mention is used.
--game \<*game*\>         | A game ID or name.
--node \<*node*\>         | A permission node (see core module documentation on permissions).
(--role) \<*role*\>       | A role ID, mention or name.<br>The `--role` flag is optional if a role mention is used.
(--user) \[*user*\]       | Can be a user ID, mention or name.<br>If no *user* is provided, this will refer to the current user.<br>The `--user` flag is optional if a user mention is used.

## Global options

There are a few options that work with every command.

Option | Description
-------|------------
--dm   | Forces Geobot to reply via direct message instead of replying in the current channel.
--help | Will execute the help command for the current input. This is useful for checking what the command would do before executing it.
--rm   | Geobot will attempt to remove the message containing the command after processing it.

## Alternative syntax

Many commands or keywords can be expressed in multiple ways.
Below are some common aliases or synonyms you can use in many commands.
Each command may define its own alternative syntax in case it deviates from those listed here.

Keyword     | Alternatives
------------|-------------
--*keyword* | //*keyword*
-*keyword*  | /*keyword*
--all       | -a
--channel   | -c
--help      | /?
--node      | -n
--role      | -r
--server    | -s<br>--guild<br>-g
--user      | -u<br>--member<br>-m
get         | display<br>list<br>show
