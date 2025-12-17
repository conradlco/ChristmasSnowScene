package org.conradlco.christmas.snowscene.ui;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.conradlco.christmas.snowscene.model.SnowFlake;

public class SnowScenePanel extends JPanel {

  private static final int NUMBER_OF_SNOWFLAKES = 450;
  private static final int FLAKE_MAX_AGE_IN_TICKS = 2000;
  private static final int NUMBER_OF_BAUBLES = 20;

  private static final long serialVersionUID = 1L;

  private SecureRandom random = new SecureRandom();
  private TreeShape treeShape;
  private List<SnowFlake> inFlightSnowFlakes = new ArrayList<>();
  private int width;
  private int height;
  private long tickCount = 0;

  public SnowScenePanel(int width, int height) {
    this.width = width;
    this.height = height;

    // Initialize snow scene panel
    this.setBackground(Color.black);

    for (int i = 0; i < NUMBER_OF_SNOWFLAKES; i++) {
      inFlightSnowFlakes.add(getInitialSnowFlake());
    }

    treeShape = new TreeShape(NUMBER_OF_BAUBLES);
  }

  public SnowFlake getInitialSnowFlake() {
    final int heightAboveTheScreen = 450;
    return getRandomSnowFlake(random.nextInt(heightAboveTheScreen + 100) - heightAboveTheScreen);
  }

  public SnowFlake getRandomSnowFlake(int y) {
    int x = random.nextInt(1000);
    int depth = random.nextInt(1, SnowFlake.MAX_DEPTH);
    int fallSpeed = random.nextInt(1, 2); // Random fall speed between 1 and 3
    return new SnowFlake(x, y, depth, fallSpeed);
  }

  public void tick() {
    tickCount++;

    // Every second clear old flakes
    if (tickCount % 20 == 0) {
      if (inFlightSnowFlakes.removeIf(snowFlake -> snowFlake.getAge() > FLAKE_MAX_AGE_IN_TICKS)) {
        System.out.println("Removed old snowflakes. Remaining: " + inFlightSnowFlakes.size());
      }
    }

    inFlightSnowFlakes.forEach(SnowFlake::fall);

    int bottomMargin = 42;
    List<SnowFlake> flakesToBeStopped =
        inFlightSnowFlakes.stream()
            .filter(snowFlake -> snowFlake.isFalling() && snowFlake.getY() > height - bottomMargin)
            .toList();
    int stoppedCount = flakesToBeStopped.size();
    flakesToBeStopped.forEach(SnowFlake::atRest);

    // Add new flakes to replace stopped ones
    for (int i = 0; i < stoppedCount; i++) {
      inFlightSnowFlakes.add(getRandomSnowFlake(random.nextInt(50)));
    }

    repaint();
  }

  @Override
  protected void paintComponent(java.awt.Graphics g) {
    super.paintComponent(g);

    int treeDepth = 2;

    // Draw snowflakes behind scene
    for (SnowFlake snowFlake :
        inFlightSnowFlakes.stream().filter(flake -> flake.getDepth() > treeDepth).toList()) {
      snowFlake.paint(g);
    }

    treeShape.paintTree(g, getWidth(), getHeight());

    // Draw snowflakes on top of scene
    for (SnowFlake snowFlake :
        inFlightSnowFlakes.stream().filter(flake -> flake.getDepth() <= treeDepth).toList()) {
      snowFlake.paint(g);
    }
  }
}
