package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class DynamicLightTransformer extends Transformer {

    @Override
    public String getTransformClass() {
        return "atomicstryker.dynamiclights.client.DynamicLights";
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("addLightSource")) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.AALOAD, 0));
                list.add(
                        new MethodInsnNode(
                                Opcodes.INVOKEINTERFACE,
                                "atomicstryker/dynamiclights/client/IDynamicLightSource",
                                "getAttachmentEntity",
                                "()Lnet/minecraft/entity/Entity;",
                                true));
                LabelNode loopSkip = new LabelNode();
                list.add(new JumpInsnNode(Opcodes.IFNULL, loopSkip));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, "sexy/poke/Pokepatch", "blacklistedLightDims", "[i"));
                list.add(new VarInsnNode(Opcodes.ASTORE, 1));
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new InsnNode(Opcodes.ARRAYLENGTH));
                list.add(new VarInsnNode(Opcodes.ISTORE, 2));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new VarInsnNode(Opcodes.ISTORE, 3));
                LabelNode loopBegin = new LabelNode();
                list.add(loopBegin);
                list.add(new VarInsnNode(Opcodes.ILOAD, 3));
                list.add(new VarInsnNode(Opcodes.ILOAD, 2));
                list.add(new JumpInsnNode(Opcodes.IF_ICMPGE, loopSkip));
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new VarInsnNode(Opcodes.ILOAD, 3));
                list.add(new InsnNode(Opcodes.IALOAD));
                list.add(new VarInsnNode(Opcodes.ISTORE, 4));

                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(
                        new MethodInsnNode(
                                Opcodes.INVOKEINTERFACE,
                                "atomicstryker/dynamiclights/client/IDynamicLightSource",
                                "getAttachmentEntity",
                                "()Lnet/minecraft/entity/Entity;",
                                true));
                list.add(
                        new FieldInsnNode(
                                Opcodes.GETFIELD,
                                "net/minecraft/entity/Entity",
                                "field_70170_p",
                                "Lnet/minecraft/world/World;"));// world
                list.add(
                        new FieldInsnNode(
                                Opcodes.GETFIELD,
                                "net/minecraft/world/World",
                                "field_73011_w",
                                "Lnet/minecraft/world/WorldProvider;"));// worldProvider
                list.add(
                        new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/WorldProvider", "field_76574_g", "I"));// dimension
                list.add(new VarInsnNode(Opcodes.ILOAD, 4));

                LabelNode loopNextItt = new LabelNode();
                list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, loopNextItt));
                list.add(new InsnNode(Opcodes.RETURN));
                list.add(loopNextItt);

                list.add(new IincInsnNode(3, 1));
                list.add(new JumpInsnNode(Opcodes.GOTO, loopBegin));

                list.add(loopSkip);

                mn.instructions.insert(list);
            }
        }

        return getNodeBytes(cn);
    }

    /*
     * public static void test(IDynamicLightSource add) { if (add.getAttachmentEntity() != null) { for (int i :
     * Pokepatch.blacklistedLightDims) if (add.getAttachmentEntity().worldObj.provider.dimensionId == i) return; }
     * System.out.println("done"); } static interface IDynamicLightSource { Entity getAttachmentEntity(); }
     */
}
