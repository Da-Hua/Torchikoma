package dev.dubhe.torchikoma.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TorchikomaEntity extends TamableAnimal {

    protected TorchikomaEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    public void tick(){
        super.tick();
        Player player = (Player) this.getOwner();
        float f = getOwnerDistance();
        if (f > 6.0F) {
            double d0 = (player.getX() - this.getX()) / (double)f;
            double d1 = (player.getY() - this.getY()) / (double)f;
            double d2 = (player.getZ() - this.getZ()) / (double)f;
            this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
        } else {
            this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            Vec3 vec3 = (new Vec3(player.getX() - this.getX(), player.getY() - this.getY(), player.getZ() - this.getZ())).normalize().scale(Math.max(f - 2.0F, 0.0F));
            this.getNavigation().moveTo(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z, this.followLeashSpeed());
        }
    }

    protected float getOwnerDistance() {
        Player player = (Player) this.getOwner();
        if (player != null){
            return this.distanceTo(player);
        }else {
            return 0;
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }
}