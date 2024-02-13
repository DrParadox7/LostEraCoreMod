package sexy.poke.mixins.late.GrimoireOfGaia;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gaia.entity.EntityAttributes;
import gaia.entity.EntityMobBase;
import gaia.entity.monster.EntityGaiaSharko;

@Mixin(EntityGaiaSharko.class)
public class MixinEntitySharko extends EntityMobBase {

    public MixinEntitySharko(World par1World) {
        super(par1World);
    }

    /**
     * @author DrParadox
     * @reason Removes constant regen while wet
     */
    @Overwrite
    public void onLivingUpdate() {
        if (this.isInWater()) {
            this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 100, 0));
        }

        if (this.getHealth() <= EntityAttributes.maxHealth2 * 0.25F) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100, 0));
        }

        super.onLivingUpdate();
    }
}
