@CheckInfo(
   type = CheckType.FLY,
   subType = "D",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -5.0D,
   maxViolations = 25
)
public class FlyD extends PacketCheck {
   private Double lastY = null;
   private boolean jumping = false;
   private int jumped;
   private int lastBypassTick = -10;

   public void handle(VPacket vPacket, long l) {
      if (vPacket instanceof VPacketPlayInFlying) {
         VPacketPlayInFlying vPacketPlayInFlying = (VPacketPlayInFlying)vPacket;
         if (!vPacketPlayInFlying.isGround() && this.playerData.getVelocityTicks() > this.playerData.getPingTicks() * 2 && this.playerData.getTeleportTicks() > this.playerData.getPingTicks() && !this.playerData.isFlying() && this.playerData.getTotalTicks() - 10 > this.lastBypassTick && this.playerData.getWaterBucketTicks() >= this.playerData.getMaxPingTicks() * 2 && !this.playerData.isFallFlying() && !this.playerData.isGliding() && (!NMSManager.getInstance().getServerVersion().after(ServerVersion.v1_11_R1) || !BukkitUtil.hasEffect(this.player, 25))) {
            if (this.lastY != null) {
               if (this.jumping && vPacketPlayInFlying.getY() < this.lastY) {
                  int n = this.jumped++;
                  if (n > 1) {
                     World world = this.player.getWorld();
                     Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).add(new Cuboid(-0.5D, 0.5D, -0.5D, 1.5D, -0.5D, 0.5D));
                     int n2 = this.playerData.getTotalTicks();
                     this.run(() -> {
                        if (cuboid.checkBlocks(this.player, world, (material) -> {
                           return !MaterialList.BAD_VELOCITY.contains(material);
                        })) {
                           this.handleViolation("", (double)(this.jumped - 1));
                        } else {
                           this.jumped = 0;
                           this.violations -= Math.min(this.violations + 1.0D, 0.05D);
                           this.lastBypassTick = n2;
                        }

                     });
                  }

                  this.jumping = false;
               } else if (vPacketPlayInFlying.getY() > this.lastY) {
                  this.jumping = true;
               }
            }
         } else if (MathUtil.onGround(this.playerData.getLocation().getY()) || (this.playerData.getLocation().getY() - 0.41999998688697815D) % 1.0D > 1.0E-15D) {
            this.jumped = 0;
            this.jumping = false;
         }

         this.violations -= Math.min(this.violations + 5.0D, 0.025D);
         if (vPacketPlayInFlying.isPos()) {
            this.lastY = vPacketPlayInFlying.getY();
         }
      }

   }
}
    