package org.unmetaphorical.pocket_lint.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class HiccupEffect extends StatusEffect {
    public HiccupEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFFC0CB);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        RegistryKey<DamageType> hiccupsKey = RegistryKey.of(
                RegistryKeys.DAMAGE_TYPE,
                Identifier.of("pocket_lint", "hiccups")
        );

        RegistryEntry<DamageType> hiccupType = entity.getWorld()
                .getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(hiccupsKey);

        entity.damage(new DamageSource(hiccupType), 1.0f);

        Vec3d velocity = entity.getVelocity();
        entity.setVelocity(velocity.x, 0.3, velocity.z);
        entity.velocityDirty = true;

        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d lookDirection = entity.getRotationVector();

            double x = entity.getX() + lookDirection.x * 1.0;
            double y = entity.getEyeY() + lookDirection.y * 1.0;
            double z = entity.getZ() + lookDirection.z * 1.0;

            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                serverWorld.spawnParticles(
                        player,
                        ParticleTypes.BUBBLE,
                        true,
                        x, y, z,
                        10,
                        0.1, 0.1, 0.1,
                        0.1
                );
            }
        }

        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}