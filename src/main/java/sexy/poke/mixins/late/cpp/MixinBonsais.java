package sexy.poke.mixins.late.cpp;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.XSTR;

import ic2.api.crops.ICropTile;

@Mixin(targets = { "com.github.bartimaeusnek.cropspp.crops.cpp.Bonsais$InternalVanillaBonsais" }, remap = false)
public class MixinBonsais {

    @Final
    @Shadow(remap = false)
    int maxChance;
    @Shadow(remap = false)
    int[] chances;
    @Shadow(remap = false)
    ItemStack[] gain;

    /**
     * @author DrParadox7
     * @reason Provide valid XSTR class in absence of GT5 for GT4 support
     */
    @Overwrite()
    public ItemStack getGain(ICropTile crop) {
        XSTR rand = new XSTR();
        int roll = rand.nextInt(maxChance);
        for (int i = chances.length - 1; i >= 0; i--) {
            if (chances[i] >= roll) {
                ItemStack ret = gain[i].copy();
                ret.stackSize += rand.nextInt(7) - 3;
                return ret.stackSize <= 0 ? null : ret;
            }
        }
        return null;
    }
}
