package sexy.poke.mixins.late.aroma;

import net.minecraft.init.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import aroma1997.world.dimension.BiomeGenMining;

@Mixin(value = BiomeGenMining.class, remap = false)
public class MixinBiomeGenMining {

    /**
     * @author DrParadox7
     * @reason Gives a real roof to the Mining Dim
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    public void LostEra$BiomeGenMining(int par1, CallbackInfo ci) {
        ((BiomeGenMining) (Object) this).topBlock = Blocks.bedrock;
    }

}
