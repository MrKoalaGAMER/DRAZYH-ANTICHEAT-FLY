@CheckInfo(
   type = CheckType.FLY,
   subType = "I",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -2.5D,
   maxViolations = 50,
   logData = true,
   description = ""
)
public class FlyI extends MovementCheck {
   private Double lastYDiff = null;
   private int lastBypassTick;
   private int threshold;

   public void handle(PlayerLocation playerLocation, PlayerLocation currentLocation, long currentTime) {
      if (!playerLocation.getGround()) {
         double d = currentLocation.getY() - playerLocation.getY();
         if (this.lastYDiff != null && !this.playerData.isFrozen() && this.playerData.getTotalTicks() - 40 > this.lastBypassTick && this.playerData.getWaterBucketTicks() > this.playerData.getMaxPingTicks() * 2 && !this.playerData.isTeleportingV2() && !this.playerData.canFly() && this.playerData.isSpawned() && !this.playerData.isGliding() && this.playerData.getVelocityTicks() > (2 + this.playerData.getMaxPingTicks()) * 2 && !this.playerData.isLevitating() && !this.playerData.isBeingPulledBack() && !this.playerData.hasCancelledBlock() && Math.abs(d / 0.9800000190734863D + 0.08D) > 1.0E-11D && Math.abs(d + 0.9800000190734863D) > 1.0E-11D && Math.abs(d - 0.019999999105930755D) > 1.0E-9D && Math.abs(d - 0.0030162615090425504D) > 1.0E-9D && (this.playerData.getVersion().before(ClientVersion.VERSION1_9) || Math.abs(d + 0.15233518685055714D) > 1.0E-11D && Math.abs(d + 0.07242780368044421D) > 1.0E-11D)) {
            boolean bl = playerLocation.getX() != currentLocation.getX() && playerLocation.getZ() != currentLocation.getZ();
            double d2 = (this.lastYDiff - 0.08D) * 0.9800000190734863D;
            if (currentLocation.getGround() && d < 0.0D && d2 < d && MathUtil.onGround(currentLocation.getY()) || playerLocation.distanceXZSquared(currentLocation) < 0.0025D && this.player.hasPotionEffect(PotionEffectType.JUMP)) {
               d2 = d;
            } else if ((WatcherTypeLoader.isDev() || this.playerData.getVersion() != ClientVersion.VERSION1_9 && !this.player.hasPotionEffect(PotionEffectType.JUMP)) && Math.abs(d2) < 0.005D) {
               d2 = 0.0D;
            }

            double d3 = Math.abs(d2 - d);
            if (d3 > 1.0E-7D) {
               World world = this.player.getWorld();
               Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).add(new Cuboid(-0.5D, 0.5D, -1.0D, 1.5D, -0.5D, 0.5D));
               double d4 = WatcherTypeLoader.isDev() ? 0.29999D : 0.5D;
               Cuboid cuboid2 = (new Cuboid(playerLocation, currentLocation)).move(0.0D, 2.0D, 0.0D).add(new Cuboid(-d4, d4, -0.5D, 0.5D, -d4, d4));
               int n = this.playerData.getTotalTicks();
               this.run(() -> {
                  if (cuboid.checkBlocks(this.player, world, (material) -> {
                     return !MaterialList.BAD_VELOCITY.contains(material) && !MaterialList.INVALID_SHAPE.contains(material);
                  }) && cuboid2.checkBlocks(this.player, world, (material) -> {
                     return material == Material.AIR;
                  })) {
                     ++this.threshold;
                     Object[] arrobject = new Object[]{d3, d, currentLocation.getY() % 1.0D, bl};
                     this.handleViolation(String.format("D: %s D2: %s P: %s V: %s", arrobject), (bl && !this.playerData.hasLag() && !this.playerData.hasFast() ? 1.0D : 0.1D) * (double)this.threshold);
                  } else {
                     this.decreaseVL(0.1D);
                     this.lastBypassTick = n;
                     this.threshold = 0;
                  }

               });
            } else {
               this.decreaseVL(0.025D);
               this.threshold = 0;
            }
         }
      }

      this.lastYDiff = currentLocation.getGround() && playerLocation.getGround() ? null : currentLocation.getY() - playerLocation.getY();
   }
}