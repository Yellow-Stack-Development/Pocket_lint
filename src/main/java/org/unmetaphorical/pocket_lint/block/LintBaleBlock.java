package org.unmetaphorical.pocket_lint.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import org.unmetaphorical.pocket_lint.init.ModItems;

public class LintBaleBlock extends Block {
    public static final EnumProperty<BaleColor> COLOR = EnumProperty.of("color", BaleColor.class);

    public LintBaleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, BaleColor.NATURAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        net.minecraft.util.DyeColor dye = ctx.getStack().get(ModItems.COLOR_COMPONENT);
        if (dye != null) {
            return this.getDefaultState().with(COLOR, BaleColor.fromDye(dye));
        }
        return this.getDefaultState().with(COLOR, BaleColor.NATURAL);
    }

    public enum BaleColor implements StringIdentifiable {
        NATURAL("natural"),
        WHITE("white"),
        ORANGE("orange"),
        MAGENTA("magenta"),
        LIGHT_BLUE("light_blue"),
        YELLOW("yellow"),
        LIME("lime"),
        PINK("pink"),
        GRAY("gray"),
        LIGHT_GRAY("light_gray"),
        CYAN("cyan"),
        PURPLE("purple"),
        BLUE("blue"),
        BROWN("brown"),
        GREEN("green"),
        RED("red"),
        BLACK("black");

        private final String name;

        BaleColor(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static BaleColor fromDye(net.minecraft.util.DyeColor dye) {
            return BaleColor.valueOf(dye.name());
        }
    }
}
