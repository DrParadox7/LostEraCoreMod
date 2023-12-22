package sexy.poke;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import sexy.poke.mixinplugin.Mixins;

@LateMixin
public class PokepatchLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.pokepatch.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Mixins.getLateMixins(loadedMods);
    }
}
