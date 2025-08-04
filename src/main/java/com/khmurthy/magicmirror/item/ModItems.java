package com.khmurthy.magicmirror.item;

import com.khmurthy.magicmirror.MagicMirror;
import com.khmurthy.magicmirror.item.custom.MagicMirrorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item MAGIC_MIRROR = registerItem("magic_mirror", new MagicMirrorItem(new Item.Settings().maxCount(1).maxDamage(100).rarity(Rarity.UNCOMMON)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MagicMirror.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MagicMirror.LOGGER.info("Registering Mod Items for " + MagicMirror.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(MAGIC_MIRROR);
        });
    }
}
