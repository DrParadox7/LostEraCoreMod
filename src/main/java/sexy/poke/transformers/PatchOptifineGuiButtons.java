package sexy.poke.transformers;

import net.minecraft.client.gui.GuiOptionButton;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class PatchOptifineGuiButtons extends Transformer {

    @Override
    public String getTransformClass() {
        return "net.minecraft.client.gui.GuiOptionButton";
    }

    public static String[] blacklistedOptions() {
        return new String[]{"SMOOTH_FPS", "SMOOTH_WORLD", "PRELOADED_CHUNKS",
                "CHUNK_UPDATES", "CHUNK_UPDATES_DYNAMIC", "FAST_MATH",
                "LAZY_CHUNK_LOADING", "FAST_RENDER", "CHUNK_LOADING"};
    }

    public static void updateGuiButton(GuiOptionButton button) {
        if (button.returnEnumOptions() != null) {
            for (String str : blacklistedOptions()) {
                if (button.returnEnumOptions().name().equals(str)) {
                    button.enabled = false;
                    break;
                }
            }
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        System.out.println("PATCHING BUTTON");

        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.startsWith("<")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.RETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "sexy/poke/transformers/PatchOptifineGuiButtons", "updateGuiButton", "(Lnet/minecraft/client/gui/GuiOptionButton;)V", false));

                        mn.instructions.insertBefore(ain, list);
                    }
                }
            }
        }

        return getNodeBytes(cn);
    }
}
