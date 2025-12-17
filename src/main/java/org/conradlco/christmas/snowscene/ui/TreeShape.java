package org.conradlco.christmas.snowscene.ui;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class TreeShape {

  // Baubles (positions and colors) will be generated once for a given panel size
  private final java.util.List<Point> baubles = new ArrayList<>();
  private final List<Color> baubleColors = new ArrayList<>();
  private boolean baublesInitialized = false;
  private int baubleSeedWidth = -1;
  private int baubleSeedHeight = -1;
  private int baublesPerLevel;

  public TreeShape(int numberOfBaubles) {
    this.baublesPerLevel = numberOfBaubles / 4;
  }

  public void paintTree(Graphics g, int panelWidth, int panelHeight) {
    // Use Graphics2D for better quality
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int w = panelWidth;
      int h = panelHeight;
      if (w <= 0 || h <= 0) {
        return;
      }

      // Parameters for tree layout
      int levels = 4;
      int levelHeight = (int) (h * 0.14);
      int levelSpacing = (int) (h * 0.12);
      int trunkHeight = Math.max(30, (int) (h * 0.08));

      // Compute total height of the tree (levels stacked + trunk) so we can align the base to
      // bottom
      int totalLevelsHeight = (levels - 1) * levelSpacing + levelHeight;
      int totalTreeHeight = totalLevelsHeight + trunkHeight;
      int bottomPadding = 5; // small padding from bottom

      // Top Y positioned so the bottom of the trunk aligns with panel bottom - bottomPadding
      int topY = h - bottomPadding - totalTreeHeight;
      // Keep a sensible minimum topY
      topY = Math.max(8, topY);

      // Initialize baubles for current size if needed
      ensureBaublesInitialized(w, h);

      // Draw multi-level tree centered horizontally
      int centerX = w / 2;

      // Draw four triangular levels (from top -> bottom)
      for (int i = 0; i < levels; i++) {
        double levelProgress = (i + 1.0) / levels; // 0.25 .. 1.0
        int levelTopY = topY + i * levelSpacing;
        int halfWidth = (int) (w * (0.08 + 0.12 * levelProgress));

        int[] xPoints = {centerX, centerX - halfWidth, centerX + halfWidth};
        int[] yPoints = {levelTopY, levelTopY + levelHeight, levelTopY + levelHeight};

        // Slightly vary green per level for depth
        float greenBase = 0.55f - (i * 0.06f);
        g2.setColor(new Color(0f, Math.max(0f, greenBase), 0f));
        g2.fillPolygon(xPoints, yPoints, 3);

        // Add tinsel: a zig-zag line across the triangle width
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(200, 200, 200, 220)); // silver-ish
        int tinselY = levelTopY + levelHeight / 2;
        int steps = 10 + i * 4;
        for (int s = 0; s < 3; s++) { // several tinsel stripes per level
          int offsetY = tinselY + (s - 1) * 10;
          int startX = centerX - halfWidth + 10;
          int endX = centerX + halfWidth - 10;
          int segment = Math.max(4, (endX - startX) / steps);
          for (int x = startX; x < endX; x += segment) {
            int x2 = Math.min(endX, x + segment);
            int y2 = offsetY + ((x / segment) % 2 == 0 ? -6 : 6);
            g2.drawLine(x, offsetY, x2, y2);
          }
        }
      }

      // Draw trunk
      int trunkWidth = Math.max(14, (int) (w * 0.04));
      int trunkX = centerX - trunkWidth / 2;
      int trunkY = topY + totalLevelsHeight;
      g2.setColor(new Color(101, 67, 33));
      g2.fillRect(trunkX, trunkY, trunkWidth, trunkHeight);

      // Draw the star on top
      Shape star = createStar(centerX, topY - 6, Math.max(14, w / 60), Math.max(6, w / 140));
      g2.setColor(new Color(255, 215, 0)); // gold
      g2.fill(star);
      g2.setColor(new Color(200, 160, 0));
      g2.setStroke(new BasicStroke(1f));
      g2.draw(star);

      // Draw baubles
      for (int i = 0; i < baubles.size(); i++) {
        Point p = baubles.get(i);
        Color c = baubleColors.get(i);
        int radius = Math.max(6, Math.min(w / 80, 12));
        g2.setColor(c);
        g2.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
        // highlight
        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillOval(p.x - radius / 2, p.y - radius, radius / 2, radius / 2);
        // outline
        g2.setColor(Color.black);
        g2.drawOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
      }

    } finally {
      g2.dispose();
    }
  }

  private void ensureBaublesInitialized(int w, int h) {
    // default quick path
    if (baublesInitialized && w == baubleSeedWidth && h == baubleSeedHeight) {
      return;
    }
    baubles.clear();
    baubleColors.clear();

    // Use a deterministic random seeded by panel size so layout is stable for a given size
    SecureRandom seedRand = new SecureRandom(new byte[] {(byte) (w & 0xff), (byte) (h & 0xff)});
    int centerX = w / 2;

    // Match the same layout math used to draw the tree so baubles lie inside the visible triangles
    int levels = 4;
    int levelHeight = (int) (h * 0.14);
    int levelSpacing = (int) (h * 0.12);
    int trunkHeight = Math.max(30, (int) (h * 0.08));
    int totalLevelsHeight = (levels - 1) * levelSpacing + levelHeight;
    int totalTreeHeight = totalLevelsHeight + trunkHeight;
    int bottomPadding = 5;
    int topY = h - bottomPadding - totalTreeHeight;
    topY = Math.max(8, topY);

    // Compute trunk and star bounds so we can avoid placing baubles over them
    int trunkWidth = Math.max(14, (int) (w * 0.04));
    int trunkX = centerX - trunkWidth / 2;
    int trunkY = topY + totalLevelsHeight;
    Rectangle trunkRect = new Rectangle(trunkX - 2, trunkY - 2, trunkWidth + 4, trunkHeight + 4);

    int starCenterY = topY - 6;
    int starOuterRadius = Math.max(14, w / 60);
    Rectangle starRect =
        new Rectangle(
            centerX - starOuterRadius - 2,
            starCenterY - starOuterRadius - 2,
            starOuterRadius * 2 + 4,
            starOuterRadius * 2 + 4);

    // Place a few baubles on each level within the triangular area using barycentric sampling
    for (int level = 0; level < levels; level++) {
      double levelProgress = (level + 1.0) / levels;
      int levelTopY = topY + level * levelSpacing;
      int halfWidth = (int) (w * (0.08 + 0.12 * levelProgress));

      int baublesThisLevel = baublesPerLevel + seedRand.nextInt(0,3);
      for (int b = 0; b < baublesThisLevel; b++) {
        Point chosen = null;
        // Try a few times to find a point that doesn't overlap trunk or star
        int tries = 0;
        final int MAX_TRIES = 12;
        while (tries < MAX_TRIES) {
          tries++;
          // Sample uniformly inside the triangle defined by A(apex), B(left), C(right)
          double u = seedRand.nextDouble();
          double v = seedRand.nextDouble();
          if (u + v > 1.0) {
            u = 1.0 - u;
            v = 1.0 - v;
          }

          // Triangle vertices
          double ax = centerX;
          double ay = levelTopY;
          double bx = centerX - halfWidth;
          double by = levelTopY + levelHeight;
          double cx = centerX + halfWidth;
          double cy = levelTopY + levelHeight;

          double pxD = ax + u * (bx - ax) + v * (cx - ax);
          double pyD = ay + u * (by - ay) + v * (cy - ay);

          int px = (int) Math.round(pxD);
          int py = (int) Math.round(pyD);

          // If this point falls inside trunk or star bounds, reject and retry
          if (trunkRect.contains(px, py) || starRect.contains(px, py)) {
            continue;
          }

          chosen = new Point(px, py);
          break;
        }

        // If we failed to find a non-overlapping position after retries, accept last sampled point
        if (chosen == null) {
          // Final fallback: sample without checking
          double u = seedRand.nextDouble();
          double v = seedRand.nextDouble();
          if (u + v > 1.0) {
            u = 1.0 - u;
            v = 1.0 - v;
          }
          double ax = centerX;
          double ay = levelTopY;
          double bx = centerX - halfWidth;
          double by = levelTopY + levelHeight;
          double cx = centerX + halfWidth;
          double cy = levelTopY + levelHeight;
          int px = (int) Math.round(ax + u * (bx - ax) + v * (cx - ax));
          int py = (int) Math.round(ay + u * (by - ay) + v * (cy - ay));
          chosen = new Point(px, py);
        }

        baubles.add(chosen);

        // random festive color
        Color[] palette = {
          Color.red, Color.cyan, Color.magenta, Color.orange, Color.yellow, new Color(0, 200, 0)
        };
        baubleColors.add(palette[seedRand.nextInt(palette.length)]);
      }
    }

    baublesInitialized = true;
    baubleSeedWidth = w;
    baubleSeedHeight = h;
  }

  private Shape createStar(int centerX, int centerY, int outerRadius, int innerRadius) {
    GeneralPath path = new GeneralPath();
    double angle = Math.PI / 2.0; // start at top
    int points = 5;
    for (int i = 0; i < points * 2; i++) {
      double r = (i % 2 == 0) ? outerRadius : innerRadius;
      double x = centerX + Math.cos(angle) * r;
      double y = centerY - Math.sin(angle) * r;
      if (i == 0) path.moveTo(x, y);
      else path.lineTo(x, y);
      angle += Math.PI / points;
    }
    path.closePath();
    return path;
  }
}
