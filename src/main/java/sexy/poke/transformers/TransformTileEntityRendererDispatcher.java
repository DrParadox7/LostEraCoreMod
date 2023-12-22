package sexy.poke.transformers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import sexy.poke.Pokepatch;

public class TransformTileEntityRendererDispatcher extends Transformer {

    @Override
    public String getTransformClass() {
        return "net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher";
    }

    public static class AccumulatingRolling {

        private int size;
        private double total = 0d;
        private int index = 0;
        private long samples[];

        private long currentSample = 0l;

        public AccumulatingRolling(int size) {
            this.size = size;
            samples = new long[size];
            for (int i = 0; i < size; i++) samples[i] = 0;
        }

        public void add(long x) {
            currentSample += x;
        }

        public void finishSample() {
            total -= samples[index];
            samples[index] = currentSample;
            total += currentSample;
            if (++index == size) index = 0; // cheaper than modulus

            currentSample = 0;
        }

        public double getAverage() {
            return total / size;
        }
    }

    private static Class last;
    private static long time;
    public static HashMap<Class, AccumulatingRolling> map = new HashMap<>();
    public static Set<Class> rendered = new HashSet<>();

    public static void startBenchmark(TileEntity e) {
        if (!Pokepatch.showTileInfo) return;

        last = e.getClass();
        rendered.add(last);
        time = System.nanoTime();
    }

    public static void stopBenchmark() {
        if (!Pokepatch.showTileInfo) return;

        AccumulatingRolling r = map.get(last);
        if (r == null) r = new AccumulatingRolling(60 * 3);
        r.add(System.nanoTime() - time);
        map.put(last, r);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("func_147549_a")) {

                mn.instructions.insert(
                        new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "sexy/poke/transformers/TransformTileEntityRendererDispatcher",
                                "startBenchmark",
                                "(Lnet/minecraft/tileentity/TileEntity;)V",
                                false));
                mn.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));

                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.RETURN) {
                        mn.instructions.insertBefore(
                                ain,
                                new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        "sexy/poke/transformers/TransformTileEntityRendererDispatcher",
                                        "stopBenchmark",
                                        "()V",
                                        false));
                    }
                }

            }
        }

        return getNodeBytes(cn);
    }
}
