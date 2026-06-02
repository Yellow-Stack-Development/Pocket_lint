package org.unmetaphorical.pocket_lint.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class RotSicknessEffect extends StatusEffect {
    public RotSicknessEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4B5320); // Dark olive green
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {
            // Periodic damage
            entity.damage(entity.getDamageSources().magic(), 1.0f + amplifier);
            
            // Apply hunger if it's a player
            if (entity instanceof net.minecraft.entity.player.PlayerEntity player) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, amplifier));
            }

            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.MYCELIUM,
                        entity.getX(), entity.getY() + 1.0, entity.getZ(),
                        5,
                        0.3, 0.5, 0.3,
                        0.05
                );
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 25 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }
}
