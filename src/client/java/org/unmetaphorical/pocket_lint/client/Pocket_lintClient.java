package org.unmetaphorical.pocket_lint.client;

import net.fabricmc.api.ClientModInitializer;
import org.unmetaphorical.pocket_lint.init.ModBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

import org.unmetaphorical.pocket_lint.init.ModItems;

import org.unmetaphorical.pocket_lint.block.LintBaleBlock;

import org.unmetaphorical.pocket_lint.block.SoiledWoolBlock;
import org.unmetaphorical.pocket_lint.block.SoiledCarpetBlock;

public class Pocket_lintClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? 0xFFC0CB : -1, ModBlocks.SOAPY_CAULDRON);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tintIndex == 0 ? 0xFFC0CB : -1, ModBlocks.SOAPY_CAULDRON);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? 0x4B5320 : -1, ModBlocks.NASTY_CAULDRON);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tintIndex == 0 ? 0x4B5320 : -1, ModBlocks.NASTY_CAULDRON);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex == 0) {
                LintBaleBlock.BaleColor color = state.get(LintBaleBlock.COLOR);
                if (color == LintBaleBlock.BaleColor.NATURAL) return 0xFFFFFF;
                try {
                    return net.minecraft.util.DyeColor.valueOf(color.name()).getEntityColor();
                } catch (IllegalArgumentException e) {
                    return 0xFFFFFF;
                }
            }
            return -1;
        }, ModBlocks.LINT_BALE);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                net.minecraft.util.DyeColor color = stack.get(ModItems.COLOR_COMPONENT);
                return color != null ? color.getEntityColor() : 0xFFFFFF;
            }
            return -1;
        }, ModItems.LINT_BALE);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex == 0) {
                return state.get(SoiledWoolBlock.COLOR).getEntityColor();
            }
            return -1;
        }, ModBlocks.SOILED_WOOL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                net.minecraft.util.DyeColor color = stack.get(ModItems.COLOR_COMPONENT);
                return color != null ? color.getEntityColor() : 0xFFFFFF;
            }
            return -1;
        }, ModItems.SOILED_WOOL);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex == 0) {
                return state.get(SoiledCarpetBlock.COLOR).getEntityColor();
            }
            return -1;
        }, ModBlocks.SOILED_CARPET);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                net.minecraft.util.DyeColor color = stack.get(ModItems.COLOR_COMPONENT);
                return color != null ? color.getEntityColor() : 0xFFFFFF;
            }
            return -1;
        }, ModItems.SOILED_CARPET);
    }
}
