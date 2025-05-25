import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GameRenderer {
    
    public static void drawBackground(Graphics2D g2d, int screenWidth, int screenHeight, Color gridColor) {
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(15, 15, 25), 
                       screenWidth, screenHeight, new Color(25, 25, 35));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw grid
        g2d.setColor(gridColor);
        int gridSize = 50;
        for (int i = 0; i < screenWidth; i += gridSize) {
            g2d.drawLine(i, 0, i, screenHeight);
        }
        for (int j = 0; j < screenHeight; j += gridSize) {
            g2d.drawLine(0, j, screenWidth, j);
        }
    }
    
    public static void drawParticles(Graphics2D g2d, ArrayList<Particle> particles) {
        for (Particle p : particles) {
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), p.alpha));
            g2d.fillOval(p.x, p.y, p.size, p.size);
        }
    }
    
    public static void drawPlayer(Graphics2D g2d, Rectangle player, Color playerColor, int playerSize) {
        // Player glow effect
        g2d.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 50));
        g2d.fillOval(player.x - 5, player.y - 5, player.width + 10, player.height + 10);
        
        // Player main
        g2d.setColor(playerColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(player.x, player.y, player.width, player.height);
    }
    
    public static void drawBullets(Graphics2D g2d, ArrayList<Bullet> bullets, Color bulletColor, int bulletSize) {
        for (Bullet bullet : bullets) {
            // Bullet glow
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(bullet.x - 2, bullet.y - 2, bulletSize + 4, bulletSize + 4);
            
            // Bullet core
            g2d.setColor(bulletColor);
            g2d.fillOval(bullet.x, bullet.y, bulletSize, bulletSize);
        }
    }
    
    public static void drawEnemies(Graphics2D g2d, ArrayList<Enemy> enemies, Color enemyColor) {
        for (Enemy enemy : enemies) {
            // Enemy glow
            g2d.setColor(new Color(enemyColor.getRed(), enemyColor.getGreen(), enemyColor.getBlue(), 50));
            g2d.fillOval(enemy.x - 2, enemy.y - 2, enemy.width + 4, enemy.height + 4);
            
            // Enemy core
            g2d.setColor(enemyColor);
            g2d.fillOval(enemy.x, enemy.y, enemy.width, enemy.height);
        }
    }
    
    public static void drawBoss(Graphics2D g2d, Boss boss, Color bossColor, int bossHealth, int maxBossHealth, int bossSize) {
        // Boss glow
        g2d.setColor(new Color(bossColor.getRed(), bossColor.getGreen(), bossColor.getBlue(), 50));
        g2d.fillOval(boss.x - 10, boss.y - 10, boss.width + 20, boss.height + 20);
        
        // Boss main
        g2d.setColor(bossColor);
        g2d.fillOval(boss.x, boss.y, boss.width, boss.height);
        
        // Boss health bar
        int healthBarWidth = bossSize;
        int healthBarHeight = 10;
        g2d.setColor(Color.RED);
        g2d.fillRect(boss.x, boss.y - 15, healthBarWidth, healthBarHeight);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(boss.x, boss.y - 15, (int)(healthBarWidth * ((float)bossHealth / maxBossHealth)), healthBarHeight);
    }
    
    public static void drawHUD(Graphics2D g2d, int playerHealth, int maxPlayerHealth, int score, long startTime, 
                              int screenWidth, int screenHeight, int maxWidth, int maxHeight) {
        // Player health bar
        g2d.setColor(new Color(30, 30, 40));
        g2d.fillRect(20, 20, maxPlayerHealth + 4, 14);
        g2d.setColor(Color.RED);
        g2d.fillRect(22, 22, maxPlayerHealth, 10);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(22, 22, playerHealth, 10);
        
        // Score and time
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 50);
        
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g2d.drawString("Time: " + elapsedTime + "s", 20, 80);
        
        // Screen fill progress
        int progressWidth = 200;
        float fillProgress = Math.min((float)screenWidth / maxWidth, (float)screenHeight / maxHeight);
        g2d.setColor(new Color(30, 30, 40));
        g2d.fillRect(screenWidth - progressWidth - 30, 20, progressWidth + 4, 14);
        g2d.setColor(new Color(100, 100, 255));
        g2d.fillRect(screenWidth - progressWidth - 28, 22, (int)(progressWidth * fillProgress), 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Fill: " + (int)(fillProgress * 100) + "%", screenWidth - progressWidth - 30, 50);
    }
    
    public static void drawPauseScreen(Graphics2D g2d, int screenWidth, int screenHeight) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.WHITE);
        g2d.drawString("PAUSED", screenWidth / 2 - 80, screenHeight / 2);
    }
    
    public static void drawWinScreen(Graphics2D g2d, int screenWidth, int screenHeight, int score, long startTime, int timeLeft) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.YELLOW);
        g2d.drawString("YOU WIN!", screenWidth / 2 - 100, screenHeight / 2 - 30);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Final Score: " + score, screenWidth / 2 - 60, screenHeight / 2 + 20);
        
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g2d.drawString("Time: " + elapsedTime + "s", screenWidth / 2 - 40, screenHeight / 2 + 50);
        
        g2d.drawString("Click to Restart", screenWidth / 2 - 80, screenHeight / 2 + 100);
        g2d.drawString("Auto-restart in: " + timeLeft + "s", screenWidth / 2 - 100, screenHeight / 2 + 130);
    }
    
    public static void drawGameOverScreen(Graphics2D g2d, int screenWidth, int screenHeight, int score, long startTime) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.RED);
        g2d.drawString("GAME OVER", screenWidth / 2 - 120, screenHeight / 2 - 30);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + score, screenWidth / 2 - 40, screenHeight / 2 + 20);
        
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g2d.drawString("Time: " + elapsedTime + "s", screenWidth / 2 - 40, screenHeight / 2 + 50);
        
        g2d.drawString("Click to Restart", screenWidth / 2 - 80, screenHeight / 2 + 100);
    }
    
    public static void drawMainMenu(Graphics2D g2d, int screenWidth, int screenHeight, Rectangle startButton, Rectangle exitButton, Random random) {
        drawMainMenuBackground(g2d, screenWidth, screenHeight, random);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        GradientPaint titleGradient = new GradientPaint(
            screenWidth/2 - 150, screenHeight/2 - 150, new Color(100, 150, 255),
            screenWidth/2 + 150, screenHeight/2 - 100, new Color(150, 200, 255)
        );
        g2d.setPaint(titleGradient);
        g2d.drawString("FILL THE SCREEN", screenWidth/2 - 180, screenHeight/2 - 100);
        
        // Subtitle
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("Survive and expand your screen to win!", screenWidth/2 - 160, screenHeight/2 - 60);
        
        // Start button
        drawMenuButton(g2d, startButton, "START", new Color(0, 150, 0), new Color(0, 200, 0));
        
        // Exit button
        drawMenuButton(g2d, exitButton, "EXIT", new Color(150, 0, 0), new Color(200, 0, 0));
        
        // Instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(180, 180, 180));
        String[] instructions = {
            "WASD - Move",
            "Mouse - Aim and Shoot",
            "P - Pause",
            "Escape - Return to Menu",
            "CREDIT - 'NRIMIT_ANGANE'"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            g2d.drawString(instructions[i], 50, screenHeight - 120 + (i * 25));
        }
    }
    
    private static void drawMainMenuBackground(Graphics2D g2d, int screenWidth, int screenHeight, Random random) {
        // Animated background with particles
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(10, 10, 20), 
                                   screenWidth, screenHeight, new Color(20, 20, 40));
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw some animated stars/particles
        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(screenWidth);
            int y = random.nextInt(screenHeight);
            int size = random.nextInt(3) + 1;
            g2d.fillOval(x, y, size, size);
        }
        
        // Draw grid overlay
        g2d.setColor(new Color(50, 50, 70, 30));
        int gridSize = 40;
        for (int i = 0; i < screenWidth; i += gridSize) {
            g2d.drawLine(i, 0, i, screenHeight);
        }
        for (int j = 0; j < screenHeight; j += gridSize) {
            g2d.drawLine(0, j, screenWidth, j);
        }
    }
    
    private static void drawMenuButton(Graphics2D g2d, Rectangle button, String text, Color baseColor, Color hoverColor) {
        // Button background with gradient
        GradientPaint buttonGradient = new GradientPaint(
            button.x, button.y, baseColor,
            button.x, button.y + button.height, baseColor.darker()
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(button.x, button.y, button.width, button.height, 10, 10);
        
        // Button border
        g2d.setColor(hoverColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(button.x, button.y, button.width, button.height, 10, 10);
        
        // Button text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, 
            button.x + (button.width - textWidth) / 2, 
            button.y + (button.height + textHeight / 2) / 2);
    }
    
    public static void drawExplosion(Graphics2D g2d, int x, int y, int radius, Color color, int alpha) {
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        
        // Inner bright core
        g2d.setColor(new Color(255, 255, 255, alpha / 2));
        g2d.fillOval(x - radius/2, y - radius/2, radius, radius);
    }
    
    public static void drawPowerUp(Graphics2D g2d, Rectangle powerUp, Color color) {
        // Rotating power-up with glow effect
        long time = System.currentTimeMillis();
        float rotation = (time % 2000) / 2000.0f * 360;
        
        // Glow effect
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g2d.fillOval(powerUp.x - 5, powerUp.y - 5, powerUp.width + 10, powerUp.height + 10);
        
        // Main power-up
        g2d.setColor(color);
        g2d.fillRect(powerUp.x, powerUp.y, powerUp.width, powerUp.height);
        
        // Cross pattern
        g2d.setColor(Color.WHITE);
        g2d.fillRect(powerUp.x + powerUp.width/2 - 2, powerUp.y, 4, powerUp.height);
        g2d.fillRect(powerUp.x, powerUp.y + powerUp.height/2 - 2, powerUp.width, 4);
    }
    
    public static void drawAimingArrow(Graphics2D g2d, Rectangle player, Point mousePosition, int playerSize) {
        // Calculate player center
        int playerCenterX = player.x + playerSize / 2;
        int playerCenterY = player.y + playerSize / 2;
        
        // Calculate angle to mouse
        double angle = Math.atan2(mousePosition.y - playerCenterY, mousePosition.x - playerCenterX);
        
        // Arrow properties
        int arrowLength = 40;
        int arrowEndX = playerCenterX + (int)(Math.cos(angle) * arrowLength);
        int arrowEndY = playerCenterY + (int)(Math.sin(angle) * arrowLength);
        
        // Draw aiming line
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(playerCenterX, playerCenterY, arrowEndX, arrowEndY);
        
        // Draw arrowhead
        int arrowHeadSize = 8;
        double arrowHeadAngle = Math.PI / 6; // 30 degrees
        
        int arrowHead1X = arrowEndX - (int)(Math.cos(angle - arrowHeadAngle) * arrowHeadSize);
        int arrowHead1Y = arrowEndY - (int)(Math.sin(angle - arrowHeadAngle) * arrowHeadSize);
        
        int arrowHead2X = arrowEndX - (int)(Math.cos(angle + arrowHeadAngle) * arrowHeadSize);
        int arrowHead2Y = arrowEndY - (int)(Math.sin(angle + arrowHeadAngle) * arrowHeadSize);
        
        g2d.drawLine(arrowEndX, arrowEndY, arrowHead1X, arrowHead1Y);
        g2d.drawLine(arrowEndX, arrowEndY, arrowHead2X, arrowHead2Y);
        
        // Draw small dot at arrow tip for better visibility
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(arrowEndX - 2, arrowEndY - 2, 4, 4);
    }
}