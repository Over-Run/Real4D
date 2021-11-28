package org.overrun.real4d;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Timer {
    private static final long NS_PER_SECOND = 1_000_000_000L;
    private static final long MAX_NS_PER_UPDATE = 1_000_000_000L;
    private static final int MAX_TICKS_PER_UPDATE = 100;
    private final float tps;
    private long lastTime = System.nanoTime();
    public int ticks;
    public float delta;
    public float timeScale = 1;
    public float fps = 0;
    public float passedTime = 0;

    public Timer(float tps) {
        this.tps = tps;
    }

    public void advanceTime() {
        long now = System.nanoTime();
        long passedNs = now - lastTime;
        lastTime = now;
        if (passedNs < 0) {
            passedNs = 0;
        }
        if (passedNs > MAX_NS_PER_UPDATE) {
            passedNs = MAX_NS_PER_UPDATE;
        }
        fps = (float) (NS_PER_SECOND / passedNs);
        passedTime += (float) passedNs * timeScale * tps / NS_PER_SECOND;
        ticks = (int) passedTime;
        if (ticks > MAX_TICKS_PER_UPDATE) {
            ticks = MAX_TICKS_PER_UPDATE;
        }
        passedTime -= ticks;
        delta = passedTime;
    }
}
