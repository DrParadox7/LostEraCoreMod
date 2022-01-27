package sexy.poke;

import net.minecraft.block.BlockLeavesBase;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import sexy.poke.transformers.*;

import java.util.ArrayList;


public class CoreTransformer implements IClassTransformer {

    private ArrayList<Transformer> transformers = new ArrayList<>();

    public CoreTransformer() {
        System.out.println("Loading Patches");


        transformers.add(new Ic2LuminatorTransformer());
        transformers.add(new CrashReportTransformer());
        transformers.add(new PatchOptifineGuiButtons());
        transformers.add(new PatchOptifineGuiSlider());
        transformers.add(new DynamicLightTransformer());

        /*transformers.add(new TransformUpdate());
        transformers.add(new TransformRender());
        transformers.add(new TransformTileEntityRendererDispatcher());*/

        transformers.add(new HardcoreEnderFix());
     //   transformers.add(new ThaumcraftLeavesFix());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (Transformer t : transformers) {
            if (t.getTransformClass().equals(transformedName) || t.getTransformClass().equals(name)) {
                basicClass = t.transform(name, transformedName, basicClass);
            }
        }

        return basicClass;
    }


}
