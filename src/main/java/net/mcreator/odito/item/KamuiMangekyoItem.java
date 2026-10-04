package net.mcreator.odito.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.List;

public class KamuiMangekyoItem extends Item {
    private static final int PHASING_COOLDOWN = 600;
    private static final int TELEPORT_COOLDOWN = 60;
    private static final int SLASH_COOLDOWN = 120;
    private static final int PHASING_DURATION = 200;
    private static final double TELEPORT_RANGE = 32;
    private static final double SLASH_RANGE = 8;
    private static final double SLASH_DAMAGE = 8;

    public KamuiMangekyoItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            boolean isPhasing = isPhasing(stack);

            if (isPhasing) {
                setPhasing(stack, false);
                player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rPhasing disabled"), false);
            } else {
                int cooldown = getCooldown(stack);
                if (cooldown > 0) {
                    player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rPhasing cooldown: " + (cooldown / 20) + "s"), false);
                    return InteractionResultHolder.fail(stack);
                }
                setCooldown(stack, PHASING_COOLDOWN);
                setPhasing(stack, true);
                setPhasingTicks(stack, PHASING_DURATION);
                player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rPhasing activated!"), false);

                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.5, player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                }
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, net.minecraft.world.entity.Entity entity, int itemId, boolean isSelected) {
        if (world.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        if (getCooldown(stack) > 0) {
            setCooldown(stack, getCooldown(stack) - 1);
        }

        if (isPhasing(stack)) {
            int ticks = getPhasingTicks(stack);
            if (ticks <= 0) {
                setPhasing(stack, false);
                player.noPhysics = false;
            } else {
                setPhasingTicks(stack, ticks - 1);

                if (!player.isSpectator() && !player.isCreative()) {
                    player.noPhysics = true;

                    if (world instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                            ParticleTypes.PORTAL,
                            player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                            player.getY() + player.getRandom().nextDouble() * 1.8,
                            player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                            1, 0.1, 0.1, 0.1, 0.05
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean canAttackBlock(BlockState blockstate, Level world, BlockPos pos, Player player) {
        return super.canAttackBlock(blockstate, world, pos, player);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockstate) {
        if (stack.getItem() == this) {
            return 100.0F;
        }
        return super.getDestroySpeed(stack, blockstate);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        if (attacker instanceof Player player && !player.level().isClientSide) {
            performDimensionSlash(player);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    private void performDimensionSlash(Player player) {
        if (player.level().isClientSide) return;

        ItemStack stack = player.getMainHandItem();
        if (getCooldown(stack) > 0) {
            player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rSlash cooldown: " + (getCooldown(stack) / 20) + "s"), false);
            return;
        }

        setCooldown(stack, SLASH_COOLDOWN);

        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 eyePos = player.getEyePosition();
        Vec3 endPos = eyePos.add(lookVec.x * SLASH_RANGE, lookVec.y * SLASH_RANGE, lookVec.z * SLASH_RANGE);

        ClipContext context = new ClipContext(
            eyePos, endPos,
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            player
        );

        HitResult hit = player.level().clip(context);
        Vec3 hitPos = hit.getLocation() != null ? hit.getLocation() : endPos;

        List<net.minecraft.world.entity.LivingEntity> entities = player.level().getEntitiesOfClass(
            net.minecraft.world.entity.LivingEntity.class,
            new AABB(hitPos.x - 2, hitPos.y - 2, hitPos.z - 2, hitPos.x + 2, hitPos.y + 2, hitPos.z + 2),
            e -> e != player
        );

        for (net.minecraft.world.entity.LivingEntity entity : entities) {
            double dist = entity.distanceToSqr(hitPos);
            if (dist < 16) {
                DamageSource slashDamage = player.level().damageSources().playerAttack(player);
                entity.hurt(slashDamage, (float) SLASH_DAMAGE);
                entity.knockback(1.5F,
                    Mth.sin(player.getYRot() * ((float) Math.PI / 180F)),
                    -Mth.cos(player.getYRot() * ((float) Math.PI / 180F))
                );

                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANT,
                        entity.getX(), entity.getY() + 1, entity.getZ(),
                        15, 0.3, 0.3, 0.3, 0.1
                    );
                }
            }
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                serverLevel.sendParticles(
                    ParticleTypes.PORTAL,
                    hitPos.x + (player.getRandom().nextDouble() - 0.5) * 3,
                    hitPos.y + (player.getRandom().nextDouble() - 0.5) * 3,
                    hitPos.z + (player.getRandom().nextDouble() - 0.5) * 3,
                    1, 0.2, 0.2, 0.2, 0.1
                );
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5F, 0.8F);

        player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rDimension Slash!"), false);
    }

    public static void performKamuiTeleport(Player player) {
        if (player.level().isClientSide) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof KamuiMangekyoItem)) {
            return;
        }

        if (getCooldown(stack) > 0) {
            player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rTeleport cooldown: " + (getCooldown(stack) / 20) + "s"), false);
            return;
        }

        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 eyePos = player.getEyePosition();
        Vec3 endPos = eyePos.add(lookVec.x * TELEPORT_RANGE, lookVec.y * TELEPORT_RANGE, lookVec.z * TELEPORT_RANGE);

        ClipContext context = new ClipContext(
            eyePos, endPos,
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            player
        );

        HitResult hit = player.level().clip(context);

        Vec3 teleportPos;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitBlock = BlockPos.containing(hit.getLocation());
            teleportPos = new Vec3(hitBlock.getX() + 0.5, hitBlock.getY() + 1, hitBlock.getZ() + 0.5);
        } else {
            teleportPos = endPos;
        }

        BlockPos targetPos = BlockPos.containing(teleportPos);
        BlockState blockAt = player.level().getBlockState(targetPos);

        boolean canTeleport = blockAt.getFluidState().isEmpty() || isPhasing(stack);

        if (canTeleport) {
            setCooldown(stack, TELEPORT_COOLDOWN);

            if (player instanceof ServerPlayer) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                for (int i = 0; i < 20; i++) {
                    serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        player.getX() + (player.getRandom().nextDouble() - 0.5),
                        player.getY() + player.getRandom().nextDouble(),
                        player.getZ() + (player.getRandom().nextDouble() - 0.5),
                        1, 0.2, 0.2, 0.2, 0.1
                    );
                }

                player.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);

                for (int i = 0; i < 20; i++) {
                    serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        teleportPos.x + (player.getRandom().nextDouble() - 0.5),
                        teleportPos.y + player.getRandom().nextDouble(),
                        teleportPos.z + (player.getRandom().nextDouble() - 0.5),
                        1, 0.2, 0.2, 0.2, 0.1
                    );
                }
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            player.displayClientMessage(Component.literal("\u00a75[Kamui] \u00a7rTeleport!"), false);
        }
    }

    // NBT helpers — static, работают и из статических, и из обычных методов
    private static int getCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getInt("KamuiCooldown");
    }

    private static void setCooldown(ItemStack stack, int cooldown) {
        stack.getOrCreateTag().putInt("KamuiCooldown", cooldown);
    }

    private static boolean isPhasing(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("KamuiPhasing");
    }

    private static void setPhasing(ItemStack stack, boolean phasing) {
        stack.getOrCreateTag().putBoolean("KamuiPhasing", phasing);
    }

    private static int getPhasingTicks(ItemStack stack) {
        return stack.getOrCreateTag().getInt("KamuiPhasingTicks");
    }

    private static void setPhasingTicks(ItemStack stack, int ticks) {
        stack.getOrCreateTag().putInt("KamuiPhasingTicks", ticks);
    }
}
