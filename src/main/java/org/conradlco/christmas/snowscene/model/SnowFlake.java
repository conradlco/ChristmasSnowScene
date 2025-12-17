package org.conradlco.christmas.snowscene.model;

import lombok.Data;

@Data
public class SnowFlake {

  private int x;
  private int y;
  private int fallSpeed;

  public SnowFlake(int x, int y, int fallSpeed) {
    this.x = x;
    this.y = y;
    this.fallSpeed = fallSpeed;
  }

  public void fall() {
    this.y += fallSpeed;
  }
}
