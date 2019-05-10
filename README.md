# EventTimer
EventTimer records the time used by each event handler in each mod. The data is automatically logged when the game shuts down and when the commands "eventtimer log" or "eventtimerclient log" are run.

## Log file
The data is logged to "eventtimer.csv". This is a CSV file, which is, essentially, a text file where data is stored in multiple comma-separated columns. It can be opened in a normal text editor or a spreadsheet program, such as LibreOffice Calc or Microsoft Excel.

The file has the following format:

    Mod id,Event handler,Count,Total time (ns),Total time (ms)
    forge,ASM: net.minecraftforge.common.ForgeInternalHandler@5816fd13 checkSettings(Lnet/minecraftforge/fml/common/gameevent/TickEvent$ClientTickEvent:)V,4658,19807885,19
    misctweaks,ASM: coolsquid.misctweaks.handler.ModEventHandler@706b5d5e onStarve(Lnet/minecraftforge/event/entity/living/LivingAttackEvent:)V,2414,8042152,8

A complete example may be found [here](https://gist.github.com/coolsquid/d54ba73a4e8c6d428392d9d84031276d).

## Commands
EventTimer provides two commands, both of which dump the data to the log.
- /eventtimer is available in local worlds and in multiplayer worlds where the user is an OP. If used on a server, it affects the server, not the client.
	- /eventtimer log
	- /eventtimer reset
- /eventtimerclient is only available in multiplayer worlds. It affects the client, not the server.