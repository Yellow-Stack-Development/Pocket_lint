package org.unmetaphorical.pocket_lint;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.unmetaphorical.pocket_lint.block.NastyCauldronBlock;
import org.unmetaphorical.pocket_lint.block.SoapyCauldronBlock;
import org.unmetaphorical.pocket_lint.init.ModEffects;
import org.unmetaphorical.pocket_lint.init.ModItems;
import org.unmetaphorical.pocket_lint.init.ModSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.unmetaphorical.pocket_lint.event.CauldronInteractionHandler;
import org.unmetaphorical.pocket_lint.init.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.advancement.AdvancementEntry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;

public class Pocket_lint implements ModInitializer {
    public static final String MOD_ID = "pocket_lint";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<ItemGroup> SOAP_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "soap_group"));
    public static final RegistryKey<ItemGroup> ROT_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "rot_group"));
    public static final RegistryKey<ItemGroup> ALL_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "all_group"));

    public static void applyAdvancement(net.minecraft.entity.player.PlayerEntity player, String name) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            AdvancementEntry advancement = serverPlayer.getServer().getAdvancementLoader().get(Identifier.of(MOD_ID, name));
            if (advancement != null) {
                for (String criterion : advancement.value().criteria().keySet()) {
                    serverPlayer.getAdvancementTracker().grantCriterion(advancement, criterion);
                }
            }
        }
    }

    @Override
    public void onInitialize() {
        ModSounds.registerSounds();
        ModItems.registerModItems();
        ModEffects.registerEffects();

        ModBlocks.registerModBlocks();
        SoapyCauldronBlock.registerBehavior();
        NastyCauldronBlock.registerBehavior();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(entries -> {
            for (net.minecraft.util.DyeColor color : net.minecraft.util.DyeColor.values()) {
                ItemStack stack = new ItemStack(ModItems.LINT_BALE);
                stack.set(ModItems.COLOR_COMPONENT, color);
                entries.add(stack);
            }
            for (net.minecraft.util.DyeColor color : net.minecraft.util.DyeColor.values()) {
                ItemStack stack = new ItemStack(ModItems.SOILED_WOOL);
                stack.set(ModItems.COLOR_COMPONENT, color);
                entries.add(stack);
            }
            for (net.minecraft.util.DyeColor color : net.minecraft.util.DyeColor.values()) {
                ItemStack stack = new ItemStack(ModItems.SOILED_CARPET);
                stack.set(ModItems.COLOR_COMPONENT, color);
                entries.add(stack);
            }
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerItemRecipe(Items.POTION, Ingredient.ofItems(Items.GUNPOWDER), Items.SPLASH_POTION);
            builder.registerItemRecipe(Items.SPLASH_POTION, Ingredient.ofItems(Items.DRAGON_BREATH), Items.LINGERING_POTION);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (net.minecraft.entity.Entity entity : world.iterateEntities()) {
                if (entity instanceof net.minecraft.entity.ItemEntity itemEntity) {
                    CauldronInteractionHandler.handleItemTick(itemEntity);
                }
            }
        });

        Registry.register(Registries.ITEM_GROUP, SOAP_GROUP_KEY, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.SOAP))
                .displayName(Text.translatable("itemGroup.pocket_lint.soap"))
                .entries((displayContext, entries) -> {
                    entries.add(ModItems.ANIMAL_FAT);
                    entries.add(ModItems.SOAP);
                    entries.add(ModItems.LYE);
                    entries.add(ModItems.JAR_OF_WOOD_ASH);
                    entries.add(ModItems.POCKET_LINT_ITEM);
                    entries.add(ModItems.SOAPY_DISC);
                    entries.add(ModItems.LINT_PAD);
                    entries.add(ModItems.RAG);
                    entries.add(ModItems.WET_RAG);
                    entries.add(ModItems.BUTCHERS_CLEAVER);
                    
                    entries.add(PotionContentsComponent.createStack(Items.POTION, ModEffects.HICCUPS_POTION_ENTRY));
                    entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, ModEffects.HICCUPS_POTION_ENTRY));
                    entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, ModEffects.HICCUPS_POTION_ENTRY));
                })
                .build());

        Registry.register(Registries.ITEM_GROUP, ROT_GROUP_KEY, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.ROTTEN_GUNK))
                .displayName(Text.translatable("itemGroup.pocket_lint.rot"))
                .entries((displayContext, entries) -> {
                    entries.add(ModItems.UNROTTEN_FLESH);
                    entries.add(ModItems.ROTTEN_GUNK);
                    entries.add(ModItems.SOILED_RAG);
                    entries.add(ModItems.NASTY_SCRAPER);
                    
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(ModItems.SOILED_WOOL);
                        stack.set(ModItems.COLOR_COMPONENT, color);
                        entries.add(stack);
                    }
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(ModItems.SOILED_CARPET);
                        stack.set(ModItems.COLOR_COMPONENT, color);
                        entries.add(stack);
                    }
                })
                .build());

        Registry.register(Registries.ITEM_GROUP, ALL_GROUP_KEY, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.POCKET_LINT_ITEM))
                .displayName(Text.translatable("itemGroup.pocket_lint.all"))
                .entries((displayContext, entries) -> {
                    entries.add(ModItems.POCKET_LINT_ITEM);
                    entries.add(ModItems.BUTCHERS_CLEAVER);
                    entries.add(ModItems.NASTY_SCRAPER);
                    entries.add(ModItems.SOAPY_DISC);
                    entries.add(ModItems.LINT_PAD);
                    entries.add(ModItems.RAG);
                    entries.add(ModItems.WET_RAG);
                    entries.add(ModItems.SOILED_RAG);
                    entries.add(ModItems.SOAP);
                    entries.add(ModItems.ANIMAL_FAT);
                    entries.add(ModItems.LYE);
                    entries.add(ModItems.JAR_OF_WOOD_ASH);
                    entries.add(ModItems.UNROTTEN_FLESH);
                    entries.add(ModItems.ROTTEN_GUNK);

                    // Non-colored Lint Bale
                    entries.add(ModItems.LINT_BALE);

                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(ModItems.LINT_BALE);
                        stack.set(ModItems.COLOR_COMPONENT, color);
                        entries.add(stack);
                    }
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(ModItems.SOILED_WOOL);
                        stack.set(ModItems.COLOR_COMPONENT, color);
                        entries.add(stack);
                    }
                    for (DyeColor color : DyeColor.values()) {
                        ItemStack stack = new ItemStack(ModItems.SOILED_CARPET);
                        stack.set(ModItems.COLOR_COMPONENT, color);
                        entries.add(stack);
                    }
                    
                    entries.add(PotionContentsComponent.createStack(Items.POTION, ModEffects.HICCUPS_POTION_ENTRY));
                    entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, ModEffects.HICCUPS_POTION_ENTRY));
                    entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, ModEffects.HICCUPS_POTION_ENTRY));
                })
                .build());
    }
}
