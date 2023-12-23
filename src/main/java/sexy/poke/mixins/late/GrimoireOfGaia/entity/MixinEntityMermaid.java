package sexy.poke.mixins.late.GrimoireOfGaia.entity;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gaia.entity.EntityAttributes;
import gaia.entity.EntityMobBase;
import gaia.entity.monster.EntityGaiaMermaid;

@Mixin(EntityGaiaMermaid.class)
public class MixinEntityMermaid extends EntityMobBase {

    public MixinEntityMermaid(World par1World) {
        super(par1World);
    }

    /**
     * @author DrParadox
     * @reason Removes constant regen while wet and gives resistance when below 25% health
     */
    @Overwrite
    public void onLivingUpdate() {
        if (this.isInWater()) {
            this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 100, 0));
        }

        if (this.getHealth() <= EntityAttributes.maxHealth2 * 0.25F) {
            this.addPotionEffect(new PotionEffect(Potion.resistance.id, 100, 0));
        }

        super.onLivingUpdate();
    }
}
