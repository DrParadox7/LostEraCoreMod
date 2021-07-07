package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class TransformRender  extends Transformer{
    @Override
    public String getTransformClass() {
        return "net.minecraft.client.renderer.EntityRenderer";
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        ClassNode cn = getNode(basicClass);

        for(MethodNode mn : cn.methods) {
            for(AbstractInsnNode ain : mn.instructions.toArray()) {
                if(ain instanceof LdcInsnNode) {
                    LdcInsnNode ldc = (LdcInsnNode)ain;
                    if(ldc.cst instanceof String) {
                        if(ldc.cst.toString().equals("hand")) {
                            mn.instructions.insertBefore(ldc, new MethodInsnNode(Opcodes.INVOKESTATIC, "sexy/poke/Pokepatch", "render", "()V", false));
                        }
                    }
                }
            }
        }

        return getNodeBytes(cn);
    }
}
