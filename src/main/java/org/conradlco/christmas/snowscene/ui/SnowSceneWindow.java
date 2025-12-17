package org.conradlco.christmas.snowscene.ui;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SnowSceneWindow extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int TICK_RATE_MS = 50;

    private final SnowScenePanel snowScenePanel;

    // Keep the executor as a field so we can control its lifecycle
    private transient ScheduledExecutorService animationTickExecutor;

    public SnowSceneWindow() {
        setTitle("Christmas Snow Scene");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        snowScenePanel = new SnowScenePanel(getWidth(), getHeight());
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(snowScenePanel, BorderLayout.CENTER);
    }

    public void startAnimation() {
        // Create the executor if it's not running yet
        if (animationTickExecutor == null || animationTickExecutor.isShutdown()) {
            animationTickExecutor = Executors.newSingleThreadScheduledExecutor();
            animationTickExecutor.scheduleAtFixedRate(
                    snowScenePanel::tick, 0, TICK_RATE_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void dispose() {
        // Ensure executor is shutdown when window is disposed
        if (animationTickExecutor != null && !animationTickExecutor.isShutdown()) {
            animationTickExecutor.shutdownNow();
        }
        super.dispose();
    }
}

