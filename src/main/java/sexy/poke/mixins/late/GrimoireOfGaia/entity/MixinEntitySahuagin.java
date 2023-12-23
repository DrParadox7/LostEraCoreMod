package sexy.poke.mixins.late.GrimoireOfGaia.entity;

import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import gaia.GaiaItem;
import gaia.entity.EntityAttributes;
import gaia.entity.EntityMobBase;
import gaia.entity.ai.EntityAIGaiaAttackOnCollide;
import gaia.entity.monster.EntityGaiaSahuagin;

@Mixin(EntityGaiaSahuagin.class)
public class MixinEntitySahuagin extends EntityMobBase {

    public MixinEntitySahuagin(World par1World) {
        super(par1World);
    }

    @Shadow(remap = false)
    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(
            ((EntityGaiaSahuagin) (Object) this),
            1.0,
            20,
            60,
            15.0F);
    @Shadow(remap = false)
    private EntityAIGaiaAttackOnCollide aiAttackOnCollide = new EntityAIGaiaAttackOnCollide(this, 1.0, true);
    @Shadow(remap = false)
    private int switchHealth;

    /**
     * @author DrParadox
     * @reason Removes constant regen while wet
     */
    @Overwrite
    public void onLivingUpdate() {
        if (this.isInWater()) {
            this.addPotionEffect(new PotionEffect(Potion.resistance.id, 100, 0));
        }
        if (this.getHealth() < EntityAttributes.maxHealth1 * 0.25F && this.switchHealth == 0) {
            this.setCurrentItemOrArmor(0, new ItemStack(GaiaItem.PropWeapon, 1, 3));
            this.tasks.removeTask(this.aiArrowAttack);
            this.tasks.addTask(1, this.aiAttackOnCollide);
            this.switchHealth = 1;
        }

        if (this.getHealth() > EntityAttributes.maxHealth1 * 0.5F && this.switchHealth == 1) {
            this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
            this.tasks.removeTask(this.aiAttackOnCollide);
            this.tasks.addTask(1, this.aiArrowAttack);
            this.switchHealth = 0;
        }

        super.onLivingUpdate();
    }
}
