package org.unmetaphorical.pocket_lint.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.ProjectileUtil;
import org.unmetaphorical.pocket_lint.Pocket_lint;
import org.unmetaphorical.pocket_lint.init.ModItems;
import org.unmetaphorical.pocket_lint.init.ModBlocks;
import org.unmetaphorical.pocket_lint.block.SoiledWoolBlock;
import org.unmetaphorical.pocket_lint.block.SoiledCarpetBlock;

import java.util.List;

public class RagItem extends Item {

    public RagItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        context.getPlayer().setCurrentHand(context.getHand());
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTime) {
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (remainingUseTime % 8 == 0) {
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 0.8f, 1.2f);
            }

            if (user instanceof PlayerEntity player) {
                HitResult hitResult = player.raycast(5.0, 0.0f, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState state = world.getBlockState(pos);
                    if (isCleanable(state)) {
                        ParticleEffect particle = stack.isOf(ModItems.SOILED_RAG) ? ParticleTypes.SOUL : ParticleTypes.BUBBLE;
                        serverWorld.spawnParticles(particle, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.2, 0.1, 0.2, 0.05);
                    }
                }
            }
        }
    }

    private boolean isCleanable(BlockState state) {
        String blockName = state.getBlock().getTranslationKey();
        if (state.isIn(BlockTags.WOOL) && !state.isOf(Blocks.WHITE_WOOL)) return true;
        if (blockName.contains("concrete") && !blockName.contains("powder") && !state.isOf(Blocks.WHITE_CONCRETE)) return true;
        if (blockName.contains("terracotta") && !state.isOf(Blocks.TERRACOTTA)) return true;
        
        Identifier blockId = Registries.BLOCK.getId(state.getBlock());
        String path = blockId.getPath();
        for (String color : COLORS) {
            if (path.startsWith(color)) return true;
        }
        return false;
    }

    private void breakAndPush(PlayerEntity player, World world) {
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
        Vec3d pushDir = player.getRotationVec(1.0f).multiply(-1.5); // Push back significantly
        player.addVelocity(pushDir.x, 0.3, pushDir.z);
        player.velocityModified = true;
    }

    private boolean tryDamage(ItemStack stack, PlayerEntity player, World world, int amount) {
        if (player.getAbilities().creativeMode) return true;
        
        if (stack.getDamage() + amount >= stack.getMaxDamage()) {
            return false;
        }
        
        stack.setDamage(stack.getDamage() + amount);
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            double reach = 5.0;
            Vec3d start = player.getCameraPosVec(1.0F);
            Vec3d rotation = player.getRotationVec(1.0F);
            Vec3d end = start.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
            Box box = player.getBoundingBox().stretch(rotation.multiply(reach)).expand(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(player, start, end, box, (e) -> !e.isSpectator() && e.canHit(), reach * reach);

            ItemStack resultStack = stack;
            boolean broke = false;

            if (entityHitResult != null) {
                Entity entity = entityHitResult.getEntity();
                boolean acted = false;
                int damageCost = 3; // Default mob cost

                if (stack.isOf(ModItems.WET_RAG)) {
                    if (entity instanceof ZombieVillagerEntity zombieVillager) {
                        damageCost = 10;
                        if (tryDamage(stack, player, world, damageCost)) {
                            zombieVillager.convertTo(EntityType.VILLAGER, true);
                            acted = true;
                        } else {
                            broke = true;
                        }
                    } else if (entity instanceof ZombieEntity zombie) {
                        if (tryDamage(stack, player, world, damageCost)) {
                            BlockPos entityPos = zombie.getBlockPos();
                            for (int i = 0; i < 3; i++) {
                                world.spawnEntity(new ItemEntity(world, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, new ItemStack(ModItems.UNROTTEN_FLESH)));
                            }
                            zombie.discard();
                            acted = true;
                        } else {
                            broke = true;
                        }
                    } else if (entity instanceof SkeletonEntity skeleton) {
                        if (tryDamage(stack, player, world, damageCost)) {
                            BlockPos entityPos = skeleton.getBlockPos();
                            for (int i = 0; i < 3; i++) {
                                world.spawnEntity(new ItemEntity(world, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, new ItemStack(Items.BONE_MEAL)));
                            }
                            skeleton.discard();
                            acted = true;
                        } else {
                            broke = true;
                        }
                    } else if (entity instanceof CreeperEntity creeper) {
                        if (tryDamage(stack, player, world, damageCost)) {
                            BlockPos entityPos = creeper.getBlockPos();
                            world.spawnEntity(new ItemEntity(world, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, new ItemStack(Items.GUNPOWDER)));
                            world.spawnEntity(new ItemEntity(world, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, new ItemStack(Items.OAK_LEAVES)));
                            creeper.discard();
                            acted = true;
                        } else {
                            broke = true;
                        }
                    } else if (entity instanceof WitherSkeletonEntity whitherSkeleton) {
                        damageCost = 3;
                        if (tryDamage(stack, player, world, damageCost)) {
                            whitherSkeleton.convertTo(EntityType.SKELETON, true);
                            acted = true;
                        } else {
                            broke = true;
                        }
                    }
                } else if (stack.isOf(ModItems.SOILED_RAG)) {
                    if (entity instanceof VillagerEntity villager) {
                        damageCost = 10;
                        if (tryDamage(stack, player, world, damageCost)) {
                            villager.convertTo(EntityType.ZOMBIE_VILLAGER, true);
                            acted = true;
                        } else {
                            broke = true;
                        }
                    }
                }

                if (acted) {
                    Pocket_lint.applyAdvancement(player, "germaphobe");
                    world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    ParticleEffect particle = stack.isOf(ModItems.SOILED_RAG) ? ParticleTypes.SOUL : ParticleTypes.HAPPY_VILLAGER;
                    serverWorld.spawnParticles(particle, entity.getX(), entity.getY() + 1.0, entity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                    
                    if (world.getRandom().nextFloat() < 0.2f) {
                        world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ModItems.POCKET_LINT_ITEM)));
                    }
                }
            } else {
                HitResult hitResult = player.raycast(reach, 0.0f, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState state = world.getBlockState(pos);
                    String blockName = state.getBlock().getTranslationKey();
                    boolean isWool = state.isIn(BlockTags.WOOL);
                    boolean isConcrete = blockName.contains("concrete") && !blockName.contains("powder");
                    boolean isTerracotta = blockName.contains("terracotta");
                    BlockState newState = null;

                    if (stack.isOf(ModItems.WET_RAG)) {
                        if (state.isOf(ModBlocks.SOILED_WOOL)) {
                            net.minecraft.util.DyeColor color = state.get(SoiledWoolBlock.COLOR);
                            Block block = Registries.BLOCK.get(Identifier.of("minecraft", color.getName() + "_wool"));
                            if (block != Blocks.AIR) newState = block.getDefaultState();
                        } else if (state.isOf(ModBlocks.SOILED_CARPET)) {
                            net.minecraft.util.DyeColor color = state.get(SoiledCarpetBlock.COLOR);
                            Block block = Registries.BLOCK.get(Identifier.of("minecraft", color.getName() + "_carpet"));
                            if (block != Blocks.AIR) newState = block.getDefaultState();
                        } else if (isWool && !state.isOf(Blocks.WHITE_WOOL)) {
                            newState = Blocks.WHITE_WOOL.getDefaultState();
                        } else if (isConcrete && !state.isOf(Blocks.WHITE_CONCRETE)) {
                            newState = Blocks.WHITE_CONCRETE.getDefaultState();
                        } else if (isTerracotta && !state.isOf(Blocks.TERRACOTTA)) {
                            newState = Blocks.TERRACOTTA.getDefaultState();
                        } else {
                            Identifier blockId = Registries.BLOCK.getId(state.getBlock());
                            String path = blockId.getPath();
                            for (String colorStr : COLORS) {
                                if (path.startsWith(colorStr)) {
                                    String whitePath = path.replace(colorStr, "white_");
                                    Identifier whiteId = Identifier.of(blockId.getNamespace(), whitePath);
                                    Block whiteBlock = Registries.BLOCK.get(whiteId);
                                    if (whiteBlock != Blocks.AIR && !state.isOf(whiteBlock)) {
                                        newState = whiteBlock.getDefaultState();
                                    }
                                    break;
                                }
                            }
                        }
                    } else if (stack.isOf(ModItems.SOILED_RAG)) {
                        net.minecraft.util.DyeColor color = null;
                        if (state.isIn(BlockTags.WOOL)) {
                            color = getWoolColor(state);
                            if (color != null) newState = ModBlocks.SOILED_WOOL.getDefaultState().with(SoiledWoolBlock.COLOR, color);
                        } else if (state.isIn(BlockTags.WOOL_CARPETS)) {
                            color = getCarpetColor(state);
                            if (color != null) newState = ModBlocks.SOILED_CARPET.getDefaultState().with(SoiledCarpetBlock.COLOR, color);
                        }
                    }

                    if (newState != null) {
                        if (tryDamage(stack, player, world, 1)) {
                            if (stack.isOf(ModItems.WET_RAG)) {
                                Pocket_lint.applyAdvancement(player, "enwhitened");
                                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 10, 0.3, 0.2, 0.3, 0.1);
                            } else {
                                // Maybe add a 'soiled' advancement?
                                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_STEP, SoundCategory.BLOCKS, 1.0f, 0.8f);
                                serverWorld.spawnParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 10, 0.3, 0.2, 0.3, 0.1);
                            }
                            
                            world.setBlockState(pos, newState);
                            
                            if (world.getRandom().nextFloat() < 0.15f) {
                                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(ModItems.POCKET_LINT_ITEM)));
                            }
                        } else {
                            broke = true;
                        }
                    }
                }
            }

            if (broke) {
                if (stack.isOf(ModItems.WET_RAG) || stack.isOf(ModItems.SOILED_RAG)) {
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SPONGE_STEP, SoundCategory.PLAYERS, 1.0f, 0.8f);
                    return new ItemStack(ModItems.RAG);
                } else {
                    breakAndPush(player, world);
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    private net.minecraft.util.DyeColor getWoolColor(BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        String path = id.getPath();
        if (path.equals("white_wool")) return net.minecraft.util.DyeColor.WHITE;
        for (net.minecraft.util.DyeColor color : net.minecraft.util.DyeColor.values()) {
            if (path.startsWith(color.getName())) return color;
        }
        return null;
    }

    private net.minecraft.util.DyeColor getCarpetColor(BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        String path = id.getPath();
        if (path.equals("white_carpet")) return net.minecraft.util.DyeColor.WHITE;
        for (net.minecraft.util.DyeColor color : net.minecraft.util.DyeColor.values()) {
            if (path.startsWith(color.getName())) return color;
        }
        return null;
    }

    private static final List<String> COLORS = List.of(
            "red_", "blue_", "cyan_", "purple_", "magenta_", "pink_",
            "orange_", "yellow_", "lime_", "green_", "light_blue_",
            "black_", "gray_", "light_gray_", "brown_"
    );

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        if (stack.getDamage() < stack.getMaxDamage() - 1) {
            ItemStack remainder = stack.copy();
            remainder.setDamage(stack.getDamage() + 1);
            return remainder;
        }
        return ItemStack.EMPTY;
    }
}