package cc.cerial.nbultimate.utils;

import org.bukkit.Location;

public class MathUtils {
    public static Location stereoPan(Location location, float distance) {
        float angle = location.getYaw();
        if (angle < 0)
            angle = 180 + (180 - Math.abs(angle));

        double rad = Math.toRadians(angle);
        double cosValue = Math.cos(rad);
        double sinValue = Math.sin(rad);

        return location.clone().add(cosValue * distance, 0, sinValue * distance);
    }
}