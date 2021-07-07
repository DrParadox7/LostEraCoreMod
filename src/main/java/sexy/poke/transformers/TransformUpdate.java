package sexy.poke.transformers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.util.AxisAlignedBB;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import sexy.poke.Pokepatch;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TransformUpdate extends Transformer {
    @Override
    public String getTransformClass() {
        return "net.minecraft.client.renderer.RenderGlobal";
    }

    public static HashMap<AxisAlignedBB, Long> map = new HashMap<>();

    public static void blockUpdate(int x, int y, int z, int x1, int y1, int z1) {
        if (Pokepatch.showBlockUpdates)
            map.put(AxisAlignedBB.getBoundingBox(x, y, z, x1, y1, z1), System.currentTimeMillis());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("func_72725_b")) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ILOAD, 1));
                list.add(new VarInsnNode(Opcodes.ILOAD, 2));
                list.add(new VarInsnNode(Opcodes.ILOAD, 3));
                list.add(new VarInsnNode(Opcodes.ILOAD, 4));
                list.add(new VarInsnNode(Opcodes.ILOAD, 5));
                list.add(new VarInsnNode(Opcodes.ILOAD, 6));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "sexy/poke/transformers/TransformUpdate", "blockUpdate", "(IIIIII)V", false));
                mn.instructions.insert(list);
            }
        }

        return getNodeBytes(cn);
    }
}
