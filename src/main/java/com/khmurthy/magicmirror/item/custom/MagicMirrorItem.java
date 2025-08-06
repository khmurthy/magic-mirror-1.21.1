package com.khmurthy.magicmirror.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Objects;

public class MagicMirrorItem extends Item {
    private double posX = -1.0;
    private double posY = -1.0;
    private double posZ = -1.0;
    private RegistryKey<World> dimension;

    public MagicMirrorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Begin using item
        user.setCurrentHand(hand);
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 0, false, false));
        // Play sound
        world.playSoundFromEntity(user, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);

        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        // 60 ticks = 3 seconds
        return 60;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        // Animation to show while using
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        //If user is sneaking, store a new position
        if (user.isSneaking()) {
            posX = user.getX();
            posY = user.getY();
            posZ = user.getZ();
            dimension = user.getWorld().getRegistryKey();
            world.playSoundFromEntity(user, SoundEvents.ENTITY_WARDEN_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        } else{
            if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {

                // Default to world spawn if player has not set one already
                if (posY < 0.0) {
                    posX = world.getSpawnPos().getX();
                    posY = world.getSpawnPos().getY();
                    posZ = world.getSpawnPos().getZ();
                    dimension = World.OVERWORLD;
                }

                ServerWorld targetWorld = Objects.requireNonNull(serverPlayer.getServer()).getWorld(dimension != null ? dimension : World.OVERWORLD);

                if (targetWorld != null) {
                    serverPlayer.teleport(targetWorld,
                            posX,
                            posY,
                            posZ,
                            serverPlayer.getYaw(),
                            serverPlayer.getPitch());
                }

                // Damage the item
                if (!serverPlayer.getAbilities().creativeMode) {
                    stack.damage(1, serverPlayer, EquipmentSlot.MAINHAND);
                }

                // Play visual effect
                ServerWorld serverWorld = serverPlayer.getServerWorld();
                serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                        serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        30, 0.3, 0.1, 0.3, 0.0);
            }
        }

        // Play sound
        world.playSoundFromEntity(user, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.PLAYERS, 1.0f, 1.0f);

        return super.finishUsing(stack, world, user);
    }

}
