package coolsquid.eventtimer.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class EventTimerTransformer implements IClassTransformer {

	public static long timeInjected;

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraftforge.fml.common.eventhandler.ASMEventHandler")) {
			ClassNode c = createClassNode(basicClass);
			MethodNode m = getMethod(c, "invoke", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)V");
			{
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(System.class), "nanoTime",
						"()J", false));
				toInject.add(new VarInsnNode(Opcodes.LSTORE, 2));
				m.instructions.insertBefore(m.instructions.getFirst(), toInject);
			}
			{
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.LLOAD, 2));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, c.name, "readable", "Ljava/lang/String;"));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, c.name, "owner",
						"Lnet/minecraftforge/fml/common/ModContainer;"));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EventTimer.class), "update",
						"(JLjava/lang/String;Lnet/minecraftforge/fml/common/ModContainer;)V", false));
				m.instructions.insertBefore(m.instructions.getLast().getPrevious(), toInject);
			}
			timeInjected = System.nanoTime();
			EventTimer.registerShutdownHook();
			return toBytes(c);
		}
		return basicClass;
	}

	private static MethodNode getMethod(ClassNode c, String name, String desc) {
		for (MethodNode m : c.methods) {
			if (m.name.equals(name) && m.desc.equals(desc)) {
				return m;
			}
		}
		return null;
	}

	private static ClassNode createClassNode(byte[] bytes) {
		ClassNode c = new ClassNode();
		ClassReader r = new ClassReader(bytes);
		r.accept(c, ClassReader.EXPAND_FRAMES);
		return c;
	}

	private static byte[] toBytes(ClassNode c) {
		ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		c.accept(w);
		return w.toByteArray();
	}
}