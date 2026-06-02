package org.unmetaphorical.pocket_lint.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ButchersCleaverItem extends SwordItem {
    public ButchersCleaverItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof PigEntity || target instanceof CowEntity || target instanceof SheepEntity) {
            if (attacker instanceof PlayerEntity player) {
                target.damage(target.getDamageSources().playerAttack(player), 1000f);
            } else {
                target.damage(target.getDamageSources().mobAttack(attacker), 1000f);
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
