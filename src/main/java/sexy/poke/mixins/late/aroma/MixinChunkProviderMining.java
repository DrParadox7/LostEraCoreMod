package sexy.poke.mixins.late.aroma;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import aroma1997.world.dimension.ChunkProviderMining;

@Mixin(value = ChunkProviderMining.class, remap = false)
public class MixinChunkProviderMining {

    /**
     * @author DrParadox7
     * @reason Gives a real roof to the Mining Dim
     */
    @Overwrite()
    public void func_147424_a(int p_147424_1_, int p_147424_2_, Block[] blocks) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y <= 255 && y >= 0; ++y) {
                    int code = (x * 16 + z) * 256 + y;
                    blocks[code] = Blocks.stone;
                }
            }
        }

    }
}
