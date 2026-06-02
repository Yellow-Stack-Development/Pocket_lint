package org.unmetaphorical.pocket_lint.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class SoapItem extends Item {

    public SoapItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        if (stack.getDamage() < stack.getMaxDamage() - 1) {
            ItemStack remainder = stack.copy();
            remainder.setDamage(stack.getDamage() + 1);
            return remainder;
        }
        return ItemStack.EMPTY;
    }
}