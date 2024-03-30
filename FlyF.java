@CheckInfo(
   type = CheckType.FLY,
   subType = "F",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -5.0D,
   maxViolations = 25,
   logData = true
)
public class FlyF extends MovementCheck {
   private final Set<Double> whitelistedValues = ImmutableSet.of(0.15523200451660202D, 0.15523200451D, 0.23052736891296366D, 0.23052736891D);
   private Double lastYChange = null;
   private int threshold;
   private int lastBypassTick = -10;

   public void handle(PlayerLocation playerLocation, PlayerLocation currentLocation, long currentTime) {
      if (!this.playerData.canFly() && this.playerData.isSurvival() && !currentLocation.getGround() && !playerLocation.getGround() && this.playerData.getTotalTicks() - 10 > this.lastBypassTick && !this.playerData.isTeleporting(3) && this.playerData.getTickerMap().get(TickerType.TELEPORT) > 1 && (NMSManager.getInstance().getServerVersion().before(ServerVersion.v1_11_R1) || !BukkitUtil.hasEffect(this.player, 25)) && this.playerData.getVehicleTicks() > this.playerData.getPingTicks() && this.playerData.isSpawned() && !this.playerData.isLevitating() && !this.playerData.isGliding()) {
         double d = Math.abs(playerLocation.getY() - currentLocation.getY());
         if (this.lastYChange != null && d > 0.0D && playerLocation.getY() > 0.0D && currentLocation.getY() > 0.0D && !this.playerData.hasFast()) {
            if (d == this.lastYChange && (d < 0.098D || d > 0.09800001D) && !this.whitelistedValues.contains(d)) {
               World world = this.player.getWorld();
               Cuboid cuboid = (new Cuboid(currentLocation)).add(new Cuboid(-0.5D, 0.5D, -0.5D, 1.5D, -0.5D, 0.5D));
               Cuboid cuboid2 = (new Cuboid(playerLocation, currentLocation)).move(0.0D, 2.0D, 0.0D).expand(0.29999D, 0.5D, 0.29999D);
               int n = this.playerData.getTotalTicks();
               if (d == 1.0999999999999943D) {
                  this.lastBypassTick = n;
                  return;
               }

               this.run(() -> {
                  if (cuboid.checkBlocks(this.player, world, (material) -> {
                     return !MaterialList.BAD_VELOCITY.contains(material);
                  }) && cuboid2.checkBlocks(this.player, world, (material) -> {
                     return material == Material.AIR;
                  })) {
                     int n2 = ++this.threshold;
                     this.threshold = n2 + 1;
                     if (n2 > 1) {
                        Object[] arrobject = new Object[]{d};
                        if (d == 0.3135000000000048D) {
                           return;
                        }

                        if (d == 0.315000000000048D) {
                           return;
                        }

                        this.handleViolation(String.format("D: %s", arrobject), (double)this.threshold / 2.0D);
                     }
                  } else {
                     this.threshold = 0;
                     this.violations -= Math.min(this.violations + 5.0D, 0.01D);
                     this.lastBypassTick = n;
                  }

               });
            } else {
               this.violations -= Math.min(this.violations + 5.0D, 0.01D);
               this.threshold = 0;
            }
         }

         this.lastYChange = d;
      }

   }
}
    