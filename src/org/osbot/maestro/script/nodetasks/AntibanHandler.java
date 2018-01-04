package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeTimeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.AntibanCharacteristic;
import org.osbot.maestro.script.slayer.utils.AntibanFrequency;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;

import java.util.Random;

public class AntibanHandler extends NodeTimeTask {

    private final AntibanCharacteristic antibanCharacteristic = AntibanCharacteristic.getInstance();
    private final Random random;

    public AntibanHandler(AntibanFrequency antibanFrequency) {
        super(antibanFrequency.getRate(), antibanFrequency.getRateUnit(), antibanFrequency.getDeviation(),
                antibanFrequency.getDeviationUnit(), Priority.VERY_LOW);
        random = new Random();
    }

    @Override
    protected void execute() throws InterruptedException {
        switch (random.nextInt(7)) {
            case 0:
                provider.log("Antiban: Move camera pitch");
                if (provider.getCamera().getPitchAngle() != provider.getCamera().getLowestPitchAngle()) {
                    provider.getCamera().movePitch(provider.getCamera().getLowestPitchAngle());
                    break;
                }
            case 1:
                provider.log("Antiban: Move camera randomly");
                int pitch = provider.getCamera().getPitchAngle();
                int yaw = provider.getCamera().getYawAngle();
                for (int i = 0; i < antibanCharacteristic.getCameraMoveCount(); i++) {
                    provider.getCamera().movePitch(random.nextInt(2) >= 1 ? (pitch + (pitch / 2)) : (pitch - (pitch
                            / 2)));
                    provider.getCamera().moveYaw(random.nextInt(2) >= 1 ? (yaw + (yaw / 2)) : -(yaw + (yaw / 2)));
                }
                break;
            case 2:
                provider.log("Antiban: Mouse Action");
                if (provider.getMouse().isOnScreen()) {
                    provider.getMouse().moveOutsideScreen();
                    break;
                }
                provider.getMouse().move(random.nextInt(350), random.nextInt(350));
                break;
            case 3:
                provider.log("Antiban: Hover food");
                sendBroadcast(new Broadcast("hover-food-antiban"));
                break;
            case 4:
                provider.log("Antiban: Hover random inventory item");
                Item[] items = provider.getInventory().getItems();
                Item item = items[random.nextInt(items.length)];
                if (item != null) {
                    item.hover();
                }
                break;
            case 5:
                provider.log("Antiban: Check kills left");
                sendBroadcast(new Broadcast("check-kills-antiban"));
                break;
            case 6:
                provider.log("Antiban: Turning camera to random entity");
                NPC npc = provider.getNpcs().singleFilter(provider.getNpcs().getAll(), new Filter<NPC>() {
                    @Override
                    public boolean match(NPC npc) {
                        return npc != null && npc.exists() && !npc.isOnScreen();
                    }
                });
                if (npc != null && npc.exists()) {
                    provider.getCamera().toEntity(npc);
                }
                break;
            case 7:
                provider.log("Antiban: Hover target");
                sendBroadcast(new Broadcast("hover-target-antiban"));
                break;
        }
        super.execute();
        provider.log("Next Antiban action in: " + (getNextRefresh() / 1000) + " seconds");
    }
}
