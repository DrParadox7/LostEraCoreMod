package sexy.poke;

import java.util.ArrayList;

import net.minecraft.launchwrapper.IClassTransformer;

import sexy.poke.transformers.CrashReportTransformer;
import sexy.poke.transformers.DynamicLightTransformer;
import sexy.poke.transformers.HardcoreEnderFix;
import sexy.poke.transformers.Ic2LuminatorTransformer;
import sexy.poke.transformers.PatchOptifineGuiButtons;
import sexy.poke.transformers.PatchOptifineGuiSlider;
import sexy.poke.transformers.Transformer;

public class CoreTransformer implements IClassTransformer {

    private final ArrayList<Transformer> transformers = new ArrayList<>();

    public CoreTransformer() {
        System.out.println("Loading Patches");

        transformers.add(new Ic2LuminatorTransformer());
        transformers.add(new CrashReportTransformer());
        transformers.add(new PatchOptifineGuiButtons());
        transformers.add(new PatchOptifineGuiSlider());
        transformers.add(new DynamicLightTransformer());

        /*
         * transformers.add(new TransformUpdate()); transformers.add(new TransformRender()); transformers.add(new
         * TransformTileEntityRendererDispatcher());
         */

        transformers.add(new HardcoreEnderFix());
        // transformers.add(new ThaumcraftLeavesFix());
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
