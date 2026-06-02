package org.unmetaphorical.pocket_lint.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.biome.Biome;
import org.unmetaphorical.pocket_lint.Pocket_lint;
import org.unmetaphorical.pocket_lint.init.ModBlocks;
import org.unmetaphorical.pocket_lint.init.ModEffects;
import org.unmetaphorical.pocket_lint.init.ModItems;

public class NastyCauldronBlock extends LeveledCauldronBlock {
    public static final CauldronBehavior.CauldronBehaviorMap NASTY_CAULDRON_BEHAVIOR = CauldronBehavior.createMap("nasty_water");

    public NastyCauldronBlock(Settings settings) {
        super(Biome.Precipitation.NONE, NASTY_CAULDRON_BEHAVIOR, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int level = state.get(LEVEL);
        if (level > 0) {
            for (int i = 0; i < 2; i++) {
                if (random.nextInt(2) == 0) {
                    double y = pos.getY() + 0.4375 + (level * 0.1875);
                    double x = pos.getX() + 0.1 + random.nextDouble() * 0.8;
                    double z = pos.getZ() + 0.1 + random.nextDouble() * 0.8;
                    world.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.05, 0.0);
                    world.addParticle(ParticleTypes.ANGRY_VILLAGER, x, y, z, 0.0, 0.05, 0.0);
                }
            }
        }
    }

    public static void registerBehavior() {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(ModItems.ROTTEN_GUNK, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                int level = state.get(LEVEL);
                world.setBlockState(pos, ModBlocks.NASTY_CAULDRON.getDefaultState().with(LEVEL, level));
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(stack.getItem(), stack.getCount() - 1)));
                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 0.5f);
                player.incrementStat(Stats.USE_CAULDRON);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return ItemActionResult.SUCCESS;
        });

        NASTY_CAULDRON_BEHAVIOR.map().put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                ItemStack potionStack = PotionContentsComponent.createStack(Items.POTION, ModEffects.ROT_SICKNESS_POTION_ENTRY);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, potionStack));
                player.incrementStat(Stats.USE_CAULDRON);
                int level = state.get(LEVEL);
                if (level > 1) {
                    world.setBlockState(pos, state.with(LEVEL, level - 1));
                } else {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
            }
            return ItemActionResult.SUCCESS;
        });

        NASTY_CAULDRON_BEHAVIOR.map().put(ModItems.RAG, (state, world, pos, player, hand, stack) -> {
            int level = state.get(LEVEL);
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(ModItems.SOILED_RAG)));
                player.incrementStat(Stats.USE_CAULDRON);
                if (level > 1) {
                    world.setBlockState(pos, state.with(LEVEL, level - 1));
                } else {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.BLOCKS, 1.0f, 0.5f);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return ItemActionResult.SUCCESS;
        });
    }
}
