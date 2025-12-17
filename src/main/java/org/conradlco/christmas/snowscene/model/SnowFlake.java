package org.conradlco.christmas.snowscene.model;

import lombok.Data;

import java.awt.*;

@Data
public class SnowFlake {

  public static final int MAX_DEPTH = 3;

  private double x;
  private double y;
  private int depth;
  private final int speed;

  public SnowFlake(int x, int y, int depth, int speed) {
    this.x = x;
    this.y = y;
    this.depth = depth;
    this.speed = speed;
  }

  public double fallSpeed() {
    return (MAX_DEPTH - this.depth + 1) + speed * 0.5;
  }

  public int flakeSize() {
    return MAX_DEPTH - this.depth + 2;
  }

  public void fall() {
    this.y += fallSpeed();
  }

  public void paint(Graphics g) {
    int size = MAX_DEPTH - depth + 2; // Size based on depth from viewer

    g.setColor(Color.lightGray);
    g.fillOval((int) (x + 1), (int) (y + 1), size, size);
    g.setColor(Color.white);
    g.fillOval((int) x, (int) y, size, size);
  }
}
