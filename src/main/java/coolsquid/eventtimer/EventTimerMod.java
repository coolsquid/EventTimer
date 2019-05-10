package coolsquid.eventtimer;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Most of the actual work is done in the {@link coolsquid.eventtimer.asm} package.
 * This class merely registers the commands.
 */
@Mod(modid = EventTimerMod.MODID, name = EventTimerMod.NAME, version = EventTimerMod.VERSION,
		dependencies = EventTimerMod.DEPENDENCIES, acceptableRemoteVersions = "*",
		acceptedMinecraftVersions = "(,1.13)")
public class EventTimerMod {

	public static final String MODID = "eventtimer";
	public static final String NAME = "EventTimer";
	public static final String VERSION = "1.0.0";
	public static final String DEPENDENCIES = "";

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new CommandEventTimer(true));
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandEventTimer(false));
	}
}
