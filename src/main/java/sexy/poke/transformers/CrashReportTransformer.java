package sexy.poke.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CrashReportTransformer extends Transformer {

    @Override
    public String getTransformClass() {
        return "net.minecraft.crash.CrashReport";
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        System.out.println("PATCHING Crash");

        ClassNode cn = getNode(basicClass);

        String[] astring = new String[] { "Damn you Kaito!!!!", "Kaito strikes again", "I blame Kaito",
                "How dare you Kaito", };

        String selection = "Damn you Kaito!!!!";

        try {
            selection = astring[(int) (System.nanoTime() % (long) astring.length)];
        } catch (Throwable ignored) {}

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("func_71503_h")) {
                mn.instructions.insert(new InsnNode(Opcodes.ARETURN));
                mn.instructions.insert(new LdcInsnNode(selection));
            }
        }

        return getNodeBytes(cn);
    }
}
