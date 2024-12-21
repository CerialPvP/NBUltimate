package cc.cerial.nbultimate.utils;

import cc.cerial.nbultimate.NBUltimate;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cc.cerial.nbultimate.utils.Utils.format;

public class MathUtils {
    private static final Map<Double, Double> sinValues = new HashMap<>();
    private static final Map<Double, Double> cosValues = new HashMap<>();
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * Precalculates all sin/cos values for panning.<br>
     * <b>NOTE:</b> This should be called on plugin load, and then never to be called again.
     */
    public static void precalculateSinCos() {
        NBUltimate nb = NBUltimate.get();
        long t = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        int accuracy = NBUltimate.getPluginConfig().getYawAccuracy();
        nb.getLogger().info("Precalculating sin/cos values with "+accuracy+" decimal accuracy...");

        double step = 360.0d / Math.pow(10, accuracy);
        double angleRangePerThread = 360.0d / THREADS;

        for (int i = 0; i < THREADS; i++) {
            double startAngle = i * angleRangePerThread;
            double endAngle = (i + 1) * angleRangePerThread;
            executor.submit(() -> {
                for (double angle = startAngle; angle < endAngle; angle += step) {
                    double rad = Math.toRadians(angle);
                    double sin = Math.sin(rad);
                    double cos = Math.cos(rad);
                    //nb.getLogger().info(String.format("%s degrees (%s radians): sin=%s, cos=%s", angle, rad, sin, cos));
                    sinValues.put(angle, sin);
                    cosValues.put(angle, cos);
                }
            });
        }

        executor.shutdown();
        try {
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {}

        nb.getLogger().info("Calculations have been completed in "+(System.currentTimeMillis()-t)+"ms.");
    }

    public static double getSin(double angle) {
        double minDiif = Double.MAX_VALUE;
        double closestAngle = 0.0d;
        for (double key: sinValues.keySet()) {
            double diff = Math.abs(key - angle);
            if (diff < minDiif) {
                minDiif = diff;
                closestAngle = key;
            }
        }
        return sinValues.get(closestAngle);
    }

    public static double getCos(double angle) {
        double minDiif = Double.MAX_VALUE;
        double closestAngle = 0.0d;
        for (double key: cosValues.keySet()) {
            double diff = Math.abs(key - angle);
            if (diff < minDiif) {
                minDiif = diff;
                closestAngle = key;
            }
        }
        return cosValues.get(closestAngle);
    }

    @SuppressWarnings({"ReassignedVariable"})
    public static Location stereoPan(Location location, float distance) {
        float angle = location.getYaw();
        if (angle < 0)
            angle = 180 + (180 - Math.abs(angle));

        int accuracy = NBUltimate.getPluginConfig().getYawAccuracy();
        angle = (float) Utils.roundDecimal(angle, accuracy);
        
        double cosValue = getCos(angle);
        double sinValue = getSin(angle);

        return location.clone().add(cosValue * distance, 0, sinValue * distance);
    }
}