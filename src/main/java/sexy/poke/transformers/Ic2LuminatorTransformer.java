package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class Ic2LuminatorTransformer extends Transformer {
    @Override
    public String getTransformClass() {
        return "ic2.core.block.wiring.TileEntityLuminator";
    }


    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        System.out.println("PATCHING IC2");

        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("func_145845_h")) {

                System.out.println("patching function");

                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    //extra lazy so just gonna use the line numbers
                    if (ain instanceof LineNumberNode) {
                        LineNumberNode lnn = (LineNumberNode) ain;
                        if (lnn.line == 158 || lnn.line == 166) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "ic2/core/block/wiring/TileEntityLuminator", "onLoaded", "()V", false));
                            list.add(new InsnNode(Opcodes.RETURN));
                            mn.instructions.insertBefore(lnn.start, list);
                        }
                    }
                }
            }
        }

        return getNodeBytes(cn);
    }
}
