package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class HardcoreEnderFix  extends Transformer {
    @Override
    public String getTransformClass() {
        return "chylex.hee.tileentity.TileEntityAbstractSynchronized";
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        ClassNode cn = getNode(basicClass);

        for(MethodNode mn : cn.methods) {
            if(mn.name.equals("onDataPacket")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    //extra lazy so just gonna use the line numbers
                    if (ain instanceof LineNumberNode) {
                        LineNumberNode lnn = (LineNumberNode) ain;
                        if (lnn.line == 24) {
                            mn.instructions.insert(ain, new InsnNode(Opcodes.RETURN));
                        }
                    }
                }
            }
        }
        return getNodeBytes(cn);
    }
}
