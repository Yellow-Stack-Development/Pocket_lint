package org.unmetaphorical.pocket_lint.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.unmetaphorical.pocket_lint.block.SoiledWoolBlock;
import org.unmetaphorical.pocket_lint.block.SoiledCarpetBlock;
import org.unmetaphorical.pocket_lint.block.LintBaleBlock;
import org.unmetaphorical.pocket_lint.block.SoapyCauldronBlock;
import org.unmetaphorical.pocket_lint.block.NastyCauldronBlock;

public class ModBlocks {
    public static final Block SOAPY_CAULDRON = Registry.register(
            Registries.BLOCK,
            Identifier.of("pocket_lint", "soapy_cauldron"),
            new SoapyCauldronBlock(AbstractBlock.Settings.copy(Blocks.WATER_CAULDRON))
    );

    public static final Block NASTY_CAULDRON = Registry.register(
            Registries.BLOCK,
            Identifier.of("pocket_lint", "nasty_cauldron"),
            new NastyCauldronBlock(AbstractBlock.Settings.copy(Blocks.WATER_CAULDRON))
    );

    public static final Block LINT_BALE = Registry.register(
            Registries.BLOCK,
            Identifier.of("pocket_lint", "lint_bale"),
            new LintBaleBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL))
    );

    public static final Block SOILED_WOOL = Registry.register(
            Registries.BLOCK,
            Identifier.of("pocket_lint", "soiled_wool"),
            new SoiledWoolBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL))
    );

    public static final Block SOILED_CARPET = Registry.register(
            Registries.BLOCK,
            Identifier.of("pocket_lint", "soiled_carpet"),
            new SoiledCarpetBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CARPET))
    );

    public static void registerModBlocks() {}
}