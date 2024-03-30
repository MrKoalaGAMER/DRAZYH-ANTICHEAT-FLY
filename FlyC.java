@CheckInfo(
   type = CheckType.FLY,
   subType = "C",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -1.0D,
   maxViolations = 20,
   logData = true,
   kickHighPing = true,
   description = ""
)
public class FlyC extends PacketCheck {
   private int lastBypassTick = -10;
   private Double lastY = null;
   private int threshold;

   public void handle(VPacket vPacket, long n) {
      if (vPacket instanceof VPacketPlayInFlying) {
         VPacketPlayInFlying vPacketPlayInFlying = (VPacketPlayInFlying)vPacket;
         if (this.lastY != null) {
            double n2 = vPacketPlayInFlying.isPos() ? vPacketPlayInFlying.getY() : this.lastY;
            if (this.lastY == n2 && n2 > 0.0D && !this.playerData.isVehicle() && !vPacketPlayInFlying.isGround() && !this.playerData.canFly() && !this.playerData.hasCancelledBlock() && !this.playerData.isTeleportingV2() && !this.playerData.isLevitating() && !this.playerData.isGliding() && (NMSManager.getInstance().getServerVersion().before(ServerVersion.v1_11_R1) || !BukkitUtil.hasEffect(this.player, 25)) && this.player.getGameMode().getValue() != 3 & this.playerData.getWaterBucketTicks() > this.playerData.getMaxPingTicks() * 2 && this.playerData.getLastPlaceUnderTicks() > this.playerData.getMaxPingTicks() * 2 && this.playerData.getTotalTicks() - 20 > this.lastBypassTick && this.playerData.getVelocityTicks() > this.playerData.getMaxPingTicks() && this.playerData.isSpawned() && !ServerTickTask.getInstance().isLagging(n)) {
               this.run(() -> {
                  Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).add(new Cuboid(-0.5D, 0.5D, 0.0D, 2.0D, -0.5D, 0.5D));
                  World world = this.player.getWorld();
                  boolean b = this.playerData.hasLag() || this.playerData.hasFast();
                  if (cuboid.checkBlocks(this.player, world, (material) -> {
                     return !MaterialList.BAD_VELOCITY.contains(material);
                  })) {
                     if (this.threshold++ > (b ? 4 : 1)) {
                        if (b) {
                           this.threshold = 0;
                        }

                        this.handleViolation(String.format("Y: %.2f", n2), (double)(this.threshold - 1));
                     }
                  } else {
                     this.threshold = 0;
                     this.violations -= Math.min(this.violations + 1.0D, 0.01D);
                     this.lastBypassTick = this.playerData.getTotalTicks();
                  }

               });
            } else {
               this.run(() -> {
                  this.threshold = 0;
                  this.violations -= Math.min(this.violations + 1.0D, 0.01D);
               });
            }
         }

         if (vPacketPlayInFlying.isPos()) {
            this.lastY = vPacketPlayInFlying.getY();
         }
      }

   }
}
    