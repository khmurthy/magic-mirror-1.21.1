package com.khmurthy.magicmirror.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class MagicMirrorItem extends Item {

    public MagicMirrorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Begin using item
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        // 60 ticks = 3 seconds
        return 60;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        // Animation to show while using (e.g., bow draw, block, eat)
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            BlockPos spawnPos = serverPlayer.getSpawnPointPosition();
            RegistryKey<World> spawnDim = serverPlayer.getSpawnPointDimension();

            // Default to world spawn if player has no bed set
            if (spawnPos == null) {
                spawnPos = world.getSpawnPos();
                spawnDim = World.OVERWORLD;
            }

            ServerWorld targetWorld = Objects.requireNonNull(serverPlayer.getServer()).getWorld(spawnDim != null ? spawnDim : World.OVERWORLD);

            if (targetWorld != null) {
                serverPlayer.teleport(targetWorld,
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        serverPlayer.getYaw(),
                        serverPlayer.getPitch());
            }

            // Damage the item
            //if (!serverPlayer.getAbilities().creativeMode) {
            //    stack.damage(1, user, user.);
            //}

            // Play portal effect
            ServerWorld serverWorld = serverPlayer.getServerWorld();
            serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                    serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                    30, 0.3, 0.1, 0.3, 0.0);
        }

        // Play portal sound
        world.playSoundFromEntity(user, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.PLAYERS, 1.0f, 1.0f);

        return super.finishUsing(stack, world, user);
    }

}
