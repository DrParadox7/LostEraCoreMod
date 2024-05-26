package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class DynamicLightTransformer extends Transformer {

    @Override
    public String getTransformClass() {
        return "atomicstryker.dynamiclights.client.adaptors.PlayerSelfAdaptor";
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        try {
            ClassNode classNode = getNode(basicClass);
            for (MethodNode method : classNode.methods) {
                if (method.name.equals("onTick")) {

                    AbstractInsnNode targetNode = null;
                    for (AbstractInsnNode instruction : method.instructions.toArray()) {
                        if (instruction.getOpcode() == Opcodes.IF_ICMPEQ) {
                            targetNode = instruction;
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            list.add(
                                    new FieldInsnNode(
                                            Opcodes.GETFIELD,
                                            "atomicstryker/dynamiclights/client/modules/PlayerSelfLightSource",
                                            "thePlayer",
                                            "Lnet/minecraft/entity/player/EntityPlayer;"));
                            list.add(
                                    new MethodInsnNode(
                                            Opcodes.INVOKEVIRTUAL,
                                            "net/minecraft/entity/player/EntityPlayer",
                                            "func_70027_ad",
                                            "()Z",
                                            false));
                            list.add(new JumpInsnNode(Opcodes.IFNE, ((JumpInsnNode) instruction).label));
                            method.instructions.insert(list);
                            break;
                        }
                    }
                    if (targetNode == null) {
                        System.out.println("No ASM Target found for DynamicLightTransformer");
                    } else {
                        System.out.println("Fixed strobing light with DynamicLightTransformer");
                    }

                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return basicClass;
    }

}
