package org.unmetaphorical.pocket_lint.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import org.unmetaphorical.pocket_lint.init.ModItems;

public class SoiledCarpetBlock extends CarpetBlock {
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

    public SoiledCarpetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, DyeColor.WHITE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        DyeColor color = ctx.getStack().get(ModItems.COLOR_COMPONENT);
        if (color != null) {
            return this.getDefaultState().with(COLOR, color);
        }
        return super.getPlacementState(ctx);
    }
}
