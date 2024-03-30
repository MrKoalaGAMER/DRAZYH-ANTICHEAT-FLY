@CheckInfo(
   type = CheckType.FLY,
   subType = "E",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -3.5D,
   maxViolations = 30,
   logData = true
)
public class FlyE extends MovementCheck {
   private int lastBypassTick = -10;
   private double threshold;

   public void handle(PlayerLocation playerLocation, PlayerLocation currentLocation, long currentTime) {
      if (!this.playerData.isFrozen() && this.playerData.getTotalTicks() - 20 >= this.lastBypassTick) {
         if (playerLocation.getGround() && currentLocation.getY() > playerLocation.getY() && this.playerData.isSpawned() && !this.playerData.isFallFlying() && this.playerData.getWaterBucketTicks() > this.playerData.getMaxPingTicks() * 2 && !this.playerData.hasCancelledBlock() && !this.playerData.isTeleportingV2() && !this.playerData.canFly()) {
            double d = currentLocation.getY() - playerLocation.getY();
            double d2 = this.playerData.getVersion() == ClientVersion.VERSION1_9 ? 0.419999986886978D : 0.41999998688697815D;
            boolean bl2 = this.playerData.getVelocityTicks() <= (this.playerData.getMaxPingTicks() + 1) * 4;
            if (this.playerData.getVelocityQueue().stream().anyMatch((velocity) -> {
               return Math.abs(velocity.getOriginalY() - d) <= 1.25E-4D;
            })) {
               return;
            }

            if (d < d2 && (currentLocation.getY() - d2) % 1.0D > 1.0E-15D) {
               World world = this.player.getWorld();
               Cuboid cuboid = (new Cuboid(playerLocation, currentLocation)).move(0.0D, 2.0D, 0.0D).expand(0.5D, 0.5D, 0.5D);
               Cuboid cuboid2 = (new Cuboid(playerLocation, currentLocation)).move(0.0D, -0.25D, 0.0D).expand(0.5D, 0.75D, 0.5D);
               int n = this.playerData.getTotalTicks();
               if (BukkitUtil.hasEffect(this.player, 8)) {
                  this.lastBypassTick = n;
               }

               this.run(() -> {
                  if (this.playerData.getTotalTicks() - 20 >= this.lastBypassTick) {
                     if (cuboid.checkBlocks(this.player, world, (material) -> {
                        return material == Material.AIR;
                     }) && cuboid2.checkBlocks(this.player, world, (material) -> {
                        return !MaterialList.INVALID_SHAPE.contains(material) && !MaterialList.BAD_VELOCITY.contains(material);
                     })) {
                        Iterator var9 = this.player.getNearbyEntities(2.5D, 2.5D, 2.5D).iterator();

                        Entity entity;
                        do {
                           if (!var9.hasNext()) {
                              this.threshold += bl2 ? 0.25D : 1.0D;
                              Object[] arrobject = new Object[]{d, currentLocation.getY()};
                              this.handleViolation(String.format("D: %s Y: %s", arrobject), this.threshold);
                              return;
                           }

                           entity = (Entity)var9.next();
                        } while(!(entity instanceof Boat) && !(entity instanceof Minecart));

                        this.threshold = 0.0D;
                        this.decreaseVL(0.025D);
                        this.lastBypassTick = n - 100;
                     } else {
                        this.threshold = 0.0D;
                        this.decreaseVL(0.025D);
                        this.lastBypassTick = n;
                     }
                  }
               });
            } else {
               this.threshold = 0.0D;
               this.decreaseVL(0.025D);
            }
         }

      }
   }
}