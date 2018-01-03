package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTimeTask;
import org.osbot.maestro.script.slayer.utils.AntibanCharacteristic;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AntibanHandler extends NodeTimeTask {

    private final AntibanCharacteristic antibanCharacteristic = AntibanCharacteristic.getInstance();
    private final Random random;

    public AntibanHandler() {
        super(3, TimeUnit.MINUTES, 90, TimeUnit.SECONDS);
        random = new Random();
    }

    @Override
    protected void execute() throws InterruptedException {
        //move mouse off screen
        //hover food
        //hover potion
        //camera event 3 direction random

        //Do through broadcasts? send request to hover after next pot

        switch (random.nextInt(1)) {
            case 0:
                provider.log("Antiban: Camera action");
                int pitch = provider.getCamera().getPitchAngle();
                int yaw = provider.getCamera().getYawAngle();
                for (int i = 0; i < antibanCharacteristic.getCameraMoveCount(); i++) {
                    provider.getCamera().movePitch(random.nextInt(2) >= 1 ? (pitch + (pitch / 2)) : -(pitch + (pitch / 2)));
                    provider.getCamera().moveYaw(random.nextInt(2) >= 1 ? (yaw + (yaw / 2)) : -(yaw + (yaw / 2)));
                }
                break;
            case 1:
                if (provider.getMouse().isOnScreen()) {
                    provider.getMouse().moveOutsideScreen();
                    break;
                }
                provider.getMouse().move(random.nextInt(350), random.nextInt(350));
                break;
        }
        super.execute();
    }
}
