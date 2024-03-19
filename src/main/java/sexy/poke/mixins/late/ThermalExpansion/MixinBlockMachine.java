package sexy.poke.mixins.late.ThermalExpansion;

import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper;
import cofh.thermalexpansion.util.crafting.RecipeMachine;

@Mixin(BlockMachine.class)
public class MixinBlockMachine {

    @Redirect(
            method = "postInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lcofh/thermalexpansion/plugins/nei/handlers/NEIRecipeWrapper;addMachineRecipe(Lnet/minecraft/item/crafting/IRecipe;)V",
                    ordinal = 7),
            remap = false)
    private void postInit(IRecipe iRecipe) {
        NEIRecipeWrapper.addMachineRecipe(
                new RecipeMachine(
                        BlockMachine.extruder,
                        BlockMachine.defaultAugments,
                        new Object[] { " X ", "YCY", "IPI", 'C', "thermalexpansion:machineFrame", 'I',
                                "thermalexpansion:machineCopper", 'P', TEItems.pneumaticServo, 'X', Blocks.piston, 'Y',
                                "blockGlassHardened" }));
    }
}
