package coolsquid.eventtimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Maps;

import coolsquid.eventtimer.asm.EventTimer;
import coolsquid.eventtimer.asm.EventTimerTransformer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class CommandEventTimer extends CommandBase {

	private final boolean client;

	public CommandEventTimer(boolean client) {
		this.client = client;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Available subcommands: log, reset";
	}

	@Override
	public String getName() {
		return this.client ? "eventtimerclient" : "eventtimer";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return this.client ? server == null : super.checkPermission(server, sender);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1) {
			switch (args[0]) {
			case "log":
				final Map<String, EventTimer.Entry> map = Maps.newHashMap(EventTimer.ENTRIES);
				final long timeRunning = System.nanoTime() - EventTimerTransformer.timeInjected;
				new Thread(() -> {
					final File file = new File("eventtimer.csv");
					try (PrintWriter w = new PrintWriter(file)) {
						w.println("Mod id,Event handler,Count,Total time (ns),Total time (ms)");
						w.flush();
						map.entrySet().stream().sorted((a, b) -> (int) (b.getValue().time - a.getValue().time))
								.forEach((e) -> {
									w.println((e.getValue().mod == null ? "unknown" : e.getValue().mod.getModId()) + ","
											+ e.getKey().replace(';', ':') + "," + e.getValue().count + ","
											+ e.getValue().time + ","
											+ TimeUnit.MILLISECONDS.convert(e.getValue().time, TimeUnit.NANOSECONDS));
									w.flush();
								});
						w.println();
						w.println("EventTimer has been recording for " + timeRunning + " ns");
					} catch (FileNotFoundException e1) {
						LogManager.getLogger(EventTimerMod.NAME).catching(e1);
						sender.sendMessage(new TextComponentString(
								"<EventTimer> Could not save data. More information has been printed to the log.")
										.setStyle(new Style().setColor(TextFormatting.DARK_RED)));
					}
					sender.sendMessage(new TextComponentString("[" + EventTimerMod.NAME + "] ")
							.setStyle(new Style().setColor(TextFormatting.BLUE))
							.appendSibling(new TextComponentString("Successfully saved the data to eventtimer.csv")
									.setStyle(new Style().setClickEvent(
											new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())))));
				}).start();
				break;
			case "reset":
				EventTimer.ENTRIES.clear();
				sender.sendMessage(new TextComponentString("[" + EventTimerMod.NAME + "] ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentString("Successfully reset the data.")));
				break;
			}
		} else {
			throw new WrongUsageException("%s only accepts one argument", args.length);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return args.length == 1 ? Arrays.asList("log", "reset") : super.getTabCompletions(server, sender, args, targetPos);
	}
}