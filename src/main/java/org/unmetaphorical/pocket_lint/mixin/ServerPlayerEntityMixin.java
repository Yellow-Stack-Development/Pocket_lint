package org.unmetaphorical.pocket_lint.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.unmetaphorical.pocket_lint.init.ModItems;
import org.unmetaphorical.pocket_lint.Pocket_lint;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    private int pocket_lint$walkingTicks = 0;
    @Unique
    private int pocket_lint$sneakingTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void pocket_lint$accumulateLint(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        // Passive Accumulation (Walking/Sprinting)
        double dx = player.getX() - player.prevX;
        double dz = player.getZ() - player.prevZ;
        double speed = Math.sqrt(dx * dx + dz * dz);
        
        if (player.isOnGround() && speed > 0.01) {
            pocket_lint$walkingTicks++;
            if (pocket_lint$walkingTicks >= 2000) { // Approx every 1.5 minutes of constant walking
                pocket_lint$walkingTicks = 0;
                if (player.getRandom().nextFloat() < 0.4f) {
                    pocket_lint$awardLint(player, "You found some lint in your pocket while walking.");
                }
            }
        }

        // Sneaking Accumulation
        if (player.isSneaking()) {
            pocket_lint$sneakingTicks++;
            if (pocket_lint$sneakingTicks >= 600) { // Every 30 seconds of sneaking
                pocket_lint$sneakingTicks = 0;
                if (player.getRandom().nextFloat() < 0.15f) {
                    pocket_lint$awardLint(player, "You rummaged through your pockets and found some lint.");
                }
            }
        } else {
            pocket_lint$sneakingTicks = Math.max(0, pocket_lint$sneakingTicks - 1);
        }
    }

    @Inject(method = "wakeUp", at = @At("HEAD"))
    private void pocket_lint$onWakeUp(boolean skipSleepTimer, boolean updateLevelSelectedStatus, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (!player.getWorld().isClient) {
            int amount = 1 + player.getRandom().nextInt(3);
            for (int i = 0; i < amount; i++) {
                pocket_lint$awardLint(player, null);
            }
            player.sendMessage(Text.literal("§7You woke up with fresh lint in your pockets."), true);
        }
    }

    @Unique
    private void pocket_lint$awardLint(ServerPlayerEntity player, String message) {
        ItemStack lint = new ItemStack(ModItems.POCKET_LINT_ITEM);
        if (player.getInventory().insertStack(lint)) {
            if (message != null) {
                player.sendMessage(Text.literal("§7" + message), true);
            }
            Pocket_lint.applyAdvancement(player, "root");
        }
    }
}
