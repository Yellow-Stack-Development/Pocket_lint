package org.unmetaphorical.pocket_lint.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import org.unmetaphorical.pocket_lint.effect.HiccupEffect;
import org.unmetaphorical.pocket_lint.effect.RotSicknessEffect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;

public class ModEffects {
    public static final StatusEffect HICCUPS = Registry.register(
            Registries.STATUS_EFFECT,
            Identifier.of("pocket_lint", "hiccups"),
            new HiccupEffect()
    );

    public static final RegistryEntry<StatusEffect> HICCUPS_ENTRY = Registries.STATUS_EFFECT.getEntry(HICCUPS);

    public static final StatusEffect ROT_SICKNESS = Registry.register(
            Registries.STATUS_EFFECT,
            Identifier.of("pocket_lint", "rot_sickness"),
            new RotSicknessEffect()
    );

    public static final RegistryEntry<StatusEffect> ROT_SICKNESS_ENTRY = Registries.STATUS_EFFECT.getEntry(ROT_SICKNESS);

    public static final Potion HICCUPS_POTION = Registry.register(
            Registries.POTION,
            Identifier.of("pocket_lint", "hiccups"),
            new Potion(new StatusEffectInstance(HICCUPS_ENTRY, 600, 0))
    );

    public static final RegistryEntry<Potion> HICCUPS_POTION_ENTRY = Registries.POTION.getEntry(HICCUPS_POTION);

    public static final Potion ROT_SICKNESS_POTION = Registry.register(
            Registries.POTION,
            Identifier.of("pocket_lint", "rot_sickness"),
            new Potion(new StatusEffectInstance(ROT_SICKNESS_ENTRY, 600, 0))
    );

    public static final RegistryEntry<Potion> ROT_SICKNESS_POTION_ENTRY = Registries.POTION.getEntry(ROT_SICKNESS_POTION);

    public static void registerEffects() {
    }
}