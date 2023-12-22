package sexy.poke.mixins.late.aroma;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import aroma1997.world.dimension.WorldProviderMiner;

@Mixin(value = WorldProviderMiner.class, remap = false)
public abstract class MixinWorldProviderMiner {

    /**
     * @author DrParadox
     * @reason
     */
    @Overwrite
    public double getMovementFactor() {
        return 2.0;
    }

    /**
     * @author DrParadox
     * @reason Hardcode the config to max height as the ceiling is now hardcoded to Bedrock
     */
    @Overwrite
    public int getAverageGroundLevel() {
        return 256;
    }

    @Inject(method = "registerWorldChunkManager", at = @At("TAIL"))
    public void LostEra$registerWorldChunkManager(CallbackInfo ci) {
        ((WorldProviderMiner) (Object) this).hasNoSky = true;
    }

}
