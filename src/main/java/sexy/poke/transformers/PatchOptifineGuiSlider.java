package sexy.poke.transformers;

import static sexy.poke.Pokepatch.makeAccessible;
import static sexy.poke.transformers.PatchOptifineGuiButtons.blacklistedOptions;

import java.lang.reflect.Field;

import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchOptifineGuiSlider extends Transformer {

    @Override
    public String getTransformClass() {
        return "net.minecraft.client.gui.GuiOptionSlider";
    }

    private static Field optionsField;

    public static void updateGuiSlider(GuiOptionSlider button) {
        if (optionsField == null) {
            try {
                optionsField = makeAccessible(GuiOptionSlider.class.getDeclaredField("field_146133_q"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        try {
            GameSettings.Options option = (GameSettings.Options) optionsField.get(button);

            if (option != null) {
                for (String str : blacklistedOptions()) {
                    if (option.name().equals(str)) {
                        button.enabled = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        System.out.println("PATCHING Slider");

        ClassNode cn = getNode(basicClass);

        for (MethodNode mn : cn.methods) {
            if (mn.name.startsWith("<")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.RETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(
                                new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        "sexy/poke/transformers/PatchOptifineGuiSlider",
                                        "updateGuiSlider",
                                        "(Lnet/minecraft/client/gui/GuiOptionSlider;)V",
                                        false));

                        mn.instructions.insertBefore(ain, list);
                    }
                }
            }
        }

        return getNodeBytes(cn);
    }
}
