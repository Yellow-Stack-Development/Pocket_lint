package org.unmetaphorical.pocket_lint.init;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.unmetaphorical.pocket_lint.Pocket_lint;
import org.unmetaphorical.pocket_lint.item.ButchersCleaverItem;
import org.unmetaphorical.pocket_lint.item.ModToolMaterials;
import org.unmetaphorical.pocket_lint.item.RagItem;
import org.unmetaphorical.pocket_lint.item.SoapItem;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ModItems {
    public static final FoodComponent ANIMAL_FAT_FOOD = new FoodComponent(
            1,
            0.2f,
            true,
            1.6f,
            Optional.empty(),
            List.of(new FoodComponent.StatusEffectEntry(
                    new StatusEffectInstance(ModEffects.HICCUPS_ENTRY, 600, 0), 1.0f
            )));

    public static class AnimalFatItem extends Item {
        public AnimalFatItem(Settings settings) {
            super(settings);
        }

        @Override
        public int getMaxUseTime(ItemStack stack, net.minecraft.entity.LivingEntity user) {
            return 64;
        }
    }

    public static final Item BUTCHERS_CLEAVER = registerItem("butchers_cleaver",
            new ButchersCleaverItem(ModToolMaterials.BUTCHER, 
                    new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.BUTCHER, 1, -2.4f))));

    public static final Item NASTY_SCRAPER = registerItem("nasty_scraper",
            new Item(new Item.Settings()));

    public static final Item ANIMAL_FAT = registerItem("animal_fat",
            new AnimalFatItem(new Item.Settings().component(DataComponentTypes.FOOD, ANIMAL_FAT_FOOD)));

    public static final Item SOAP = registerItem("soap",
            new SoapItem(new Item.Settings().maxDamage(4)));

    public static final Item WET_RAG = registerItem("wet_rag",
            new RagItem(new Item.Settings().maxDamage(20)));

    public static final Item RAG = registerItem("rag",
            new Item(new Item.Settings()));

    public static final Item POCKET_LINT_ITEM = registerItem("pocket_lint_item",
            new Item(new Item.Settings()));
            
    public static final Item SOAPY_DISC = registerItem("soapy_disc",
            new Item(new Item.Settings().maxCount(1).rarity(net.minecraft.util.Rarity.RARE).jukeboxPlayable(ModSounds.SOAPY_MUSIC_KEY)));

    public static final Item LINT_PAD = registerItem("lint_pad",
            new Item(new Item.Settings()));

    public static final Item UNROTTEN_FLESH = registerItem("unrotten_flesh",
            new Item(new Item.Settings()));

    public static final Item LYE = registerItem("lye",
            new Item(new Item.Settings()));

    public static final Item JAR_OF_WOOD_ASH = registerItem("jar_of_wood_ash",
            new Item(new Item.Settings()));

    public static final Item ROTTEN_GUNK = registerItem("rotten_gunk",
            new Item(new Item.Settings()));

    public static final Item SOILED_RAG = registerItem("soiled_rag",
            new RagItem(new Item.Settings().maxDamage(20)));

    public static final Item LINT_BALE = registerItem("lint_bale",
            new net.minecraft.item.BlockItem(ModBlocks.LINT_BALE, new Item.Settings()) {
                @Override
                public net.minecraft.text.Text getName(ItemStack stack) {
                    net.minecraft.util.DyeColor color = stack.get(COLOR_COMPONENT);
                    if (color != null) {
                        return net.minecraft.text.Text.translatable("block.pocket_lint.lint_bale.colored", 
                                net.minecraft.text.Text.translatable("color.minecraft." + color.getName()));
                    }
                    return super.getName(stack);
                }
            });

    public static final Item SOILED_WOOL = registerItem("soiled_wool",
            new net.minecraft.item.BlockItem(ModBlocks.SOILED_WOOL, new Item.Settings()) {
                @Override
                public net.minecraft.text.Text getName(ItemStack stack) {
                    net.minecraft.util.DyeColor color = stack.get(COLOR_COMPONENT);
                    if (color != null) {
                        return net.minecraft.text.Text.translatable("block.pocket_lint.soiled_wool.colored", 
                                net.minecraft.text.Text.translatable("color.minecraft." + color.getName()));
                    }
                    return super.getName(stack);
                }
            });

    public static final Item SOILED_CARPET = registerItem("soiled_carpet",
            new net.minecraft.item.BlockItem(ModBlocks.SOILED_CARPET, new Item.Settings()) {
                @Override
                public net.minecraft.text.Text getName(ItemStack stack) {
                    net.minecraft.util.DyeColor color = stack.get(COLOR_COMPONENT);
                    if (color != null) {
                        return net.minecraft.text.Text.translatable("block.pocket_lint.soiled_carpet.colored", 
                                net.minecraft.text.Text.translatable("color.minecraft." + color.getName()));
                    }
                    return super.getName(stack);
                }
            });

    public static final ComponentType<net.minecraft.util.DyeColor> COLOR_COMPONENT = registerComponent("color", 
            builder -> builder.codec(net.minecraft.util.DyeColor.CODEC).packetCodec(net.minecraft.util.DyeColor.PACKET_CODEC));

    private static <T> ComponentType<T> registerComponent(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Pocket_lint.MOD_ID, name), (builderOperator.apply(ComponentType.builder())).build());
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Pocket_lint.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Pocket_lint.LOGGER.info(String.format("Registering mod items for %s", Pocket_lint.MOD_ID));
    }
}
