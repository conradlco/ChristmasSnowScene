package org.conradlco.christmas.snowscene.ui;

import org.conradlco.christmas.snowscene.model.SnowFlake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SnowScenePanel extends JPanel {

  private static final int NUMBER_OF_SNOWFLAKES = 100;

  private static final long serialVersionUID = 1L;

  private List<SnowFlake> inFlightSnowFlakes = new ArrayList<>();

  public SnowScenePanel() {
    // Initialize snow scene panel
    this.setBackground(Color.black);

    for (int i = 0; i < NUMBER_OF_SNOWFLAKES; i++) {
      inFlightSnowFlakes.add(getRandomSnowFlake());
    }
  }

  public SnowFlake getRandomSnowFlake() {
    int x = (int) (Math.random() * 1000);
    int y = (int) (Math.random() * 100);
    int fallSpeed = 1 + (int) (Math.random() * 4); // Random fall speed between 1 and 3
    return new SnowFlake(x, y, fallSpeed);
  }

  public void tick() {
    // Update snow scene state here
    // For example, move snowflakes down the screen
    // Then repaint the panel to reflect changes
    inFlightSnowFlakes.forEach(SnowFlake::fall);
    inFlightSnowFlakes.removeIf(snowFlake -> snowFlake.getY() > this.getHeight());

    for (int i = inFlightSnowFlakes.size(); i < NUMBER_OF_SNOWFLAKES; i++) {
      inFlightSnowFlakes.add(getRandomSnowFlake());
    }

    repaint();
  }

  @Override
  protected void paintComponent(java.awt.Graphics g) {
    super.paintComponent(g);

    // Draw snowflakes
    for (SnowFlake snowFlake : inFlightSnowFlakes) {
      int size = 2 + snowFlake.getFallSpeed(); // Size based on fall speed
      g.setColor(Color.lightGray);
      g.fillOval(snowFlake.getX() + 1, snowFlake.getY() + 1, size, size);
      g.setColor(Color.white);
      g.fillOval(snowFlake.getX(), snowFlake.getY(), size, size);
    }
  }
}
