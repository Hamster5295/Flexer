package org.hamster5295.flexer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlexerUpdater implements Runnable {

    private final static FlexerUpdater instance = new FlexerUpdater();

    private static Thread updateThread;
    private final static long deltaTime = 10;
    private static boolean isActive = true;

    private final CopyOnWriteArrayList<Flexer> flexers = new CopyOnWriteArrayList<>();

    public FlexerUpdater() {
        updateThread = new Thread(this);
        updateThread.start();
    }

    public static void add(Flexer flexer) {
        instance.flexers.add(flexer);
    }

    public static void remove(Flexer flexer) {
        instance.flexers.remove(flexer);
    }

    public static boolean hasFlexer(Flexer flexer) {
        return instance.flexers.contains(flexer);
    }

    public static List<Flexer> getFlexers() {
        return instance.flexers;
    }

    @Override
    public void run() {
        while (isActive) {
            long startTime = System.currentTimeMillis();
            for (Flexer f : flexers) {
                if (f.getState() == Flexer.State.RUNNING)
                    f.update();
            }

            for (Flexer f : flexers) {
                if (f.getState() == Flexer.State.RUNNING)
                    f.lateUpdateTask.run(f);
            }

            long spentTime = System.currentTimeMillis() - startTime;

            try {
                //控制恒定的休眠时间
                Thread.sleep(spentTime < deltaTime ? deltaTime - spentTime : 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
