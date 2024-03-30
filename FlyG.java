@CheckInfo(
   type = CheckType.FLY,
   subType = "G",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -2.5D,
   maxViolations = 20,
   logData = true,
   description = "Flying upwards"
)
public class FlyG extends MovementCheck {
   private boolean ignoring = false;

   public void handle(PlayerLocation playerLocation, PlayerLocation currentLocation, long currentTime) {
      if (this.ignoring) {
         if (currentLocation.getGround()) {
            this.ignoring = false;
         }
      } else if (currentLocation.getY() > playerLocation.getY() && !this.playerData.isFrozen() && this.playerData.getVelocityTicks() > (this.playerData.getPingTicks() + 1) * 2 && !this.playerData.isVehicle() && !this.playerData.canFly() && !this.playerData.isGliding() && !this.playerData.isHooked() && !this.playerData.isTeleportingV2() && this.playerData.isSurvival() && !this.playerData.isFallFlying() && this.playerData.isSpawned()) {
         double d = currentLocation.getY() - Math.max(0.0D, playerLocation.getY());
         if (d > 100000.0D || this.violations > 300.0D) {
            AlertManager.getInstance().handleBan(this.playerData, this, false);
            this.playerData.disconnectForcibly();
         }

         double d2 = 0.41999998688699D;
         double d3 = Math.max(!currentLocation.getGround() ? d2 : 0.5D, d2 + (double)BukkitUtil.getPotionLevel(this.player, PotionEffectType.JUMP) * 0.2D);
         double d4 = d - d3;
         if (this.playerData.getVersion() != ClientVersion.VERSION1_7 && currentLocation.getGround() && playerLocation.getGround() && (d4 == 0.0625D || d4 == 0.10000002384185791D)) {
            return;
         }

         if (d > d3 && Math.abs(d - 0.5D) > 1.0E-12D) {
            World world = this.player.getWorld();
            Cuboid cuboid = (new Cuboid(playerLocation)).move(0.0D, -1.5D, 0.0D).expand(0.5D, 2.0D, 0.5D);
            this.run(() -> {
               if (this.playerData.getVersion() != ClientVersion.VERSION1_7 && !cuboid.checkBlocks(this.player, world, (material) -> {
                  return !material.name().equals("SLIME_BLOCK") && !material.name().equals("PISTON_EXTENSION") && !material.name().contains("SHULKER_BOX");
               })) {
                  this.ignoring = true;
               } else {
                  Object[] arrobject = new Object[]{d4};
                  this.handleViolation(String.format("D: %s", arrobject), Math.min(10.0D, 0.5D + d4));
               }

            });
         } else {
            this.violations -= Math.min(this.violations + 2.5D, 0.025D);
         }
      }

   }
}