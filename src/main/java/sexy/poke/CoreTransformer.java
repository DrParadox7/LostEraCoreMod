package sexy.poke;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import sexy.poke.transformers.CrashReportTransformer;
import sexy.poke.transformers.Ic2LuminatorTransformer;
import sexy.poke.transformers.Transformer;

import java.util.ArrayList;


public class CoreTransformer implements IClassTransformer {

    private ArrayList<Transformer> transformers = new ArrayList<>();

    public CoreTransformer() {
        transformers.add(new Ic2LuminatorTransformer());
        transformers.add(new CrashReportTransformer());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for(Transformer t : transformers) {
            if(t.getTransformClass().equals(transformedName) || t.getTransformClass().equals(name)) {
                basicClass = t.transform(name, transformedName, basicClass);
            }
        }

        return basicClass;
    }


}
