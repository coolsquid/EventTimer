package coolsquid.eventtimer.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Maps;

import coolsquid.eventtimer.EventTimerMod;
import net.minecraftforge.fml.common.ModContainer;

public class EventTimer {

	public static final Map<String, Entry> ENTRIES = new ConcurrentHashMap<>();

	public static void update(long a, String readable, ModContainer mod) {
		long b = System.nanoTime();
		Entry time = ENTRIES.get(readable);
		if (time == null) {
			time = new Entry(mod);
			ENTRIES.put(readable, time);
		}
		time.count++;
		time.time += b - a;
	}

	public static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			final Map<String, Entry> map = Maps.newHashMap(ENTRIES);
			final long timeRunning = System.nanoTime() - EventTimerTransformer.timeInjected;
			final File file = new File("eventtimer.csv");
			try (PrintWriter w = new PrintWriter(file)) {
				w.println("Mod id,Event handler,Count,Total time (ns),Total time (ms)");
				w.flush();
				map.entrySet().stream().sorted((a, b) -> (int) (b.getValue().time - a.getValue().time)).forEach((e) -> {
					w.println((e.getValue().mod == null ? "unknown" : e.getValue().mod.getModId()) + ","
							+ e.getKey().replace(';', ':') + "," + e.getValue().count + "," + e.getValue().time + ","
							+ TimeUnit.MILLISECONDS.convert(e.getValue().time, TimeUnit.NANOSECONDS));
					w.flush();
				});
				w.println();
				w.println("EventTimer has been recording for " + timeRunning + " ns");
			} catch (FileNotFoundException e1) {
				try {
					LogManager.getLogger(EventTimerMod.NAME).catching(e1);
				} catch (Exception e2) {
					e1.printStackTrace();
					throw new RuntimeException(e2);
				}
			}
		}));
	}

	public static class Entry {

		public long count, time;
		public ModContainer mod;

		public Entry(ModContainer mod) {
			this.mod = mod;
		}
	}
}