package com.khmurthy.magicmirror;

import com.khmurthy.magicmirror.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MagicMirrorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addBefore(Items.MAP, new ItemStack(ModItems.MAGIC_MIRROR));
        });
    }
}
