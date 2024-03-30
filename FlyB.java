@CheckInfo(
   type = CheckType.FLY,
   subType = "B",
   friendlyName = "Fly",
   version = CheckVersion.RELEASE,
   minViolations = -5.0D,
   maxViolations = 25,
   logData = true,
   description = "Invalid ground state"
)
public class FlyB extends MovementCheck {
   private int threshold;
   private int lastBypassTick = -10;

   public void handle(PlayerLocation playerLocation, PlayerLocation currentLocation, long currentTime) {
      if (playerLocation.getGround() && currentLocation.getGround() && playerLocation.getY() != currentLocation.getY() && !MathUtil.onGround(playerLocation.getY()) && !MathUtil.onGround(currentLocation.getY()) && this.playerData.isSpawned() && this.playerData.getTotalTicks() - 10 > this.lastBypassTick && !this.playerData.canFly() && !this.playerData.isLevitating()) {
         World world = this.player.getWorld();
         Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).expand(0.5D, 0.5D, 0.5D);
         double d = currentLocation.getY() - playerLocation.getY();
         int n = this.playerData.getTotalTicks();
         this.run(() -> {
            if (!cuboid.checkBlocks(this.player, world, (material) -> {
               return !MaterialList.INVALID_SHAPE.contains(material);
            })) {
               this.threshold = 0;
               this.violations -= Math.min(this.violations + 4.0D, 0.05D);
               this.lastBypassTick = n;
            } else {
               Iterator var6 = this.player.getNearbyEntities(2.5D, 2.5D, 2.5D).iterator();

               while(true) {
                  if (!var6.hasNext()) {
                     ++this.threshold;
                     Object[] arrobject = new Object[]{d};
                     this.handleViolation(String.format("D: %s", arrobject), (double)this.threshold);
                     break;
                  }

                  Entity entity = (Entity)var6.next();
                  if (entity instanceof Boat || entity instanceof Minecart || entity.getType().name().equalsIgnoreCase("SHULKER")) {
                     this.threshold = 0;
                     this.lastBypassTick = n - 100;
                     this.violations -= Math.min(this.violations + 4.0D, 0.05D);
                     return;
                  }
               }
            }

         });
      } else {
         this.threshold = 0;
         this.violations -= Math.min(this.violations + 4.0D, 0.05D);
      }
   }
}