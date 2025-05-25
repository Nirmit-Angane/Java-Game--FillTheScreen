import java.awt.*;
import java.util.Random;

// Bullet class
class Bullet {
    int x, y;
    double dx, dy;

    public Bullet(int x, int y, double angle, int speed) {
        this.x = x;
        this.y = y;
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
    }
    
    public void update() {
        x += dx;
        y += dy;
    }
    
    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }
    
    public boolean collidesWith(Rectangle other) {
        return x + 10 > other.x && 
               x < other.x + other.width && 
               y + 10 > other.y && 
               y < other.y + other.height;
    }
}

// Enemy class
class Enemy extends Rectangle {
    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public void moveTowards(int targetX, int targetY, float speed) {
        int dx = targetX - x;
        int dy = targetY - y;
        double angle = Math.atan2(dy, dx);
        x += (int)(speed * Math.cos(angle));
        y += (int)(speed * Math.sin(angle));
    }
}

// Boss class
class Boss extends Enemy {
    public Boss(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}

// Particle class for explosion effects
class Particle {
    int x, y;
    int size;
    Color color;
    int alpha;
    double dx, dy;
    int lifetime;
    
    public Particle(int startX, int startY, Color baseColor, Random random) {
        this.x = startX;
        this.y = startY;
        this.size = random.nextInt(5) + 2;
        this.color = baseColor;
        this.alpha = 255;
        
        double angle = random.nextDouble() * Math.PI * 2;
        double speed = random.nextDouble() * 3 + 1;
        this.dx = Math.cos(angle) * speed;
        this.dy = Math.sin(angle) * speed;
        
        this.lifetime = random.nextInt(30) + 20;
    }
    
    public void update() {
        x += dx;
        y += dy;
        alpha = Math.max(0, alpha - 5);
        lifetime--;
    }
    
    public boolean isDead() {
        return lifetime <= 0 || alpha <= 0;
    }
}