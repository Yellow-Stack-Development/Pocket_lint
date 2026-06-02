package org.unmetaphorical.pocket_lint.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.unmetaphorical.pocket_lint.Pocket_lint;
import org.unmetaphorical.pocket_lint.init.ModBlocks;
import org.unmetaphorical.pocket_lint.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;

public class CauldronInteractionHandler {

    public static void handleItemTick(ItemEntity itemEntity) {
        if (!(itemEntity.getWorld() instanceof ServerWorld world)) return;

        BlockPos pos = itemEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        PlayerEntity player = itemEntity.getOwner() instanceof PlayerEntity p ? p : null;

        if (state.isOf(Blocks.WATER_CAULDRON)) {
            int level = state.get(LeveledCauldronBlock.LEVEL);
            ItemStack stack = itemEntity.getStack();

            if (stack.isOf(ModItems.SOAP) && level > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) itemEntity.discard();
                else itemEntity.setStack(stack);

                world.setBlockState(pos, ModBlocks.SOAPY_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, level));
                world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0f, 1.2f);
                if (player != null) Pocket_lint.applyAdvancement(player, "clean_freak");
            } else if (stack.isOf(ModItems.ROTTEN_GUNK) && level > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) itemEntity.discard();
                else itemEntity.setStack(stack);

                world.setBlockState(pos, ModBlocks.NASTY_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, level));
                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 0.5f);
            }
        } else if (state.isOf(ModBlocks.SOAPY_CAULDRON)) {
            int level = state.get(LeveledCauldronBlock.LEVEL);
            ItemStack stack = itemEntity.getStack();

            if (stack.isOf(ModItems.RAG) && level > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) itemEntity.discard();
                else itemEntity.setStack(stack);

                ItemEntity ragEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, new ItemStack(ModItems.WET_RAG));
                ragEntity.setToDefaultPickupDelay();
                world.spawnEntity(ragEntity);

                if (level > 1) {
                    world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, level - 1));
                } else {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.BLOCKS, 1.0f, 0.8f);
                if (player != null) Pocket_lint.applyAdvancement(player, "from_rags_to_riches");
            }
        } else if (state.isOf(ModBlocks.NASTY_CAULDRON)) {
            int level = state.get(LeveledCauldronBlock.LEVEL);
            ItemStack stack = itemEntity.getStack();

            if (stack.isOf(ModItems.RAG) && level > 0) {
                stack.decrement(1);
                if (stack.isEmpty()) itemEntity.discard();
                else itemEntity.setStack(stack);

                ItemEntity ragEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, new ItemStack(ModItems.SOILED_RAG));
                ragEntity.setToDefaultPickupDelay();
                world.spawnEntity(ragEntity);

                if (level > 1) {
                    world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, level - 1));
                } else {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.BLOCKS, 1.0f, 0.5f);
            }
        }
    }
}
