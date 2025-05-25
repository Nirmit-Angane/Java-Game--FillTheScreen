import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FillTheScreen extends JPanel implements ActionListener, KeyListener, MouseListener {
    // Game constants
    private static final int INITIAL_WIDTH = 800, INITIAL_HEIGHT = 600;
    private static final int MAX_WIDTH = 1366, MAX_HEIGHT = 768;
    private static final int PLAYER_SIZE = 50, BULLET_SIZE = 10, ENEMY_SIZE = 20;
    private static final int BOSS_SIZE = 100;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final float ENEMY_SPEED = 2.5f;
    private static final int BOSS_SPEED = 2;
    private static int PLAYER_MAX_HEALTH = 200;
    private static final int BOSS_HEALTH = 200;
    private static final int ENEMY_SPAWN_RATE = 5; // percentage chance per frame
    private static final int BOSS_SPAWN_THRESHOLD = 10; // enemies killed to spawn boss
    
    // Colors
    private static final Color PLAYER_COLOR = new Color(0, 150, 255);
    private static final Color BULLET_COLOR = new Color(255, 255, 255, 200);
    private static final Color ENEMY_COLOR = new Color(255, 50, 50);
    private static final Color BOSS_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(10, 10, 20);
    private static final Color GRID_COLOR = new Color(30, 30, 40);
    
    // Game objects
    private final Rectangle player;
    private final ArrayList<Bullet> bullets;
    private final ArrayList<Enemy> enemies;
    private Boss boss;
    private final Random random;
    private final boolean[] keys;
    
    // Game state
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean playerWins = false;
    private int screenWidth = INITIAL_WIDTH;
    private int screenHeight = INITIAL_HEIGHT;
    private int enemiesKilled = 0;
    private int playerHealth = PLAYER_MAX_HEALTH;
    private int bossHealth = BOSS_HEALTH;
    private long startTime = 0;
    private int score = 0;
    
    // UI elements
    private final Rectangle startButton;
    private final Rectangle exitButton;
    private final JFrame frame;
    private Point mousePosition = new Point(0, 0);
    
    // Timers
    private final Timer gameTimer;
    private final Timer shootTimer;
    private final Timer winScreenTimer;
    private int winScreenTimeLeft = 10;
    private int autoShootDelay = 150;
    
    // Particle effects
    private final ArrayList<Particle> particles = new ArrayList<>();

    public FillTheScreen(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });
        setFocusable(true);

        // Initialize game objects
        player = new Rectangle(INITIAL_WIDTH / 2 - PLAYER_SIZE / 2, 
                             INITIAL_HEIGHT / 2 - PLAYER_SIZE / 2, 
                             PLAYER_SIZE, PLAYER_SIZE);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        random = new Random();
        keys = new boolean[256];

        // UI buttons
        startButton = new Rectangle(INITIAL_WIDTH / 2 - 100, INITIAL_HEIGHT / 2 - 50, 200, 50);
        exitButton = new Rectangle(INITIAL_WIDTH / 2 - 100, INITIAL_HEIGHT / 2 + 40, 200, 50);

        // Set up timers
        gameTimer = new Timer(16, this); // ~60 FPS
        
        shootTimer = new Timer(autoShootDelay, e -> {
            if (gameRunning && !gamePaused) shoot();
        });

        winScreenTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                winScreenTimeLeft--;
                if (winScreenTimeLeft <= 0) {
                    winScreenTimer.stop();
                    resetGameState();
                }
                repaint();
            }
        });
    }

    private void shoot() {
        int playerCenterX = player.x + PLAYER_SIZE / 2;
        int playerCenterY = player.y + PLAYER_SIZE / 2;
        double angle = Math.atan2(mousePosition.y - playerCenterY, mousePosition.x - playerCenterX);
        bullets.add(new Bullet(playerCenterX - BULLET_SIZE/2, playerCenterY - BULLET_SIZE/2, angle, BULLET_SPEED));
    }

    public void startGame() {
        gameRunning = true;
        gameOver = false;
        playerWins = false;
        resetGame();
        startTime = System.currentTimeMillis();
        gameTimer.start();
        shootTimer.start();
    }

    public void resetGame() {
        player.setLocation(screenWidth / 2 - PLAYER_SIZE / 2, screenHeight / 2 - PLAYER_SIZE / 2);
        bullets.clear();
        enemies.clear();
        particles.clear();
        boss = null;
        enemiesKilled = 0;
        score = 0;
        playerHealth = PLAYER_MAX_HEALTH;
        bossHealth = BOSS_HEALTH;
        screenWidth = INITIAL_WIDTH;
        screenHeight = INITIAL_HEIGHT;
        updateScreenSize();
    }
    
    private void resetGameState() {
        playerWins = false;
        gameRunning = false;
        gameOver = true;
    }
    
    private void updateScreenSize() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        revalidate();
        frame.pack();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameRunning) {
            GameRenderer.drawBackground(g2d, screenWidth, screenHeight, GRID_COLOR);
            GameRenderer.drawParticles(g2d, particles);
            GameRenderer.drawPlayer(g2d, player, PLAYER_COLOR, PLAYER_SIZE);
            GameRenderer.drawBullets(g2d, bullets, BULLET_COLOR, BULLET_SIZE);
            GameRenderer.drawEnemies(g2d, enemies, ENEMY_COLOR);
            if (boss != null) GameRenderer.drawBoss(g2d, boss, BOSS_COLOR, bossHealth, BOSS_HEALTH, BOSS_SIZE);
            GameRenderer.drawHUD(g2d, playerHealth, PLAYER_MAX_HEALTH, score, startTime, screenWidth, screenHeight, MAX_WIDTH, MAX_HEIGHT);
            if (gamePaused) GameRenderer.drawPauseScreen(g2d, screenWidth, screenHeight);
            GameRenderer.drawAimingArrow(g2d, player, mousePosition, PLAYER_SIZE);
        } else if (playerWins) {
            GameRenderer.drawWinScreen(g2d, screenWidth, screenHeight, score, startTime, winScreenTimeLeft);
        } else if (gameOver) {
            GameRenderer.drawGameOverScreen(g2d, screenWidth, screenHeight, score, startTime);
        } else {
            GameRenderer.drawMainMenu(g2d, screenWidth, screenHeight, startButton, exitButton, random);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && !gamePaused) {
            updatePlayerPosition();
            updateBullets();
            spawnEnemies();
            updateEnemies();
            updateBoss();
            updateParticles();
            checkWinCondition();
            repaint();
        }
    }
    
    private void updatePlayerPosition() {
        if (keys[KeyEvent.VK_W]) player.y = Math.max(0, player.y - PLAYER_SPEED);
        if (keys[KeyEvent.VK_S]) player.y = Math.min(screenHeight - PLAYER_SIZE, player.y + PLAYER_SPEED);
        if (keys[KeyEvent.VK_A]) player.x = Math.max(0, player.x - PLAYER_SPEED);
        if (keys[KeyEvent.VK_D]) player.x = Math.min(screenWidth - PLAYER_SIZE, player.x + PLAYER_SPEED);
    }
    
    private void updateBullets() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            
            // Remove bullets that are out of bounds
            if (bullet.isOutOfBounds(screenWidth, screenHeight)) {
                bullets.remove(i);
                continue;
            }
            
            // Check for enemy collisions
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (bullet.collidesWith(enemy)) {
                    bullets.remove(i);
                    enemies.remove(j);
                    enemiesKilled++;
                    score += 10;
                    
                    // Create explosion particles
                    createExplosion(enemy.x + ENEMY_SIZE/2, enemy.y + ENEMY_SIZE/2, ENEMY_COLOR);
                    
                    // Increase screen size
                    increaseScreenSize();
                    break;
                }
            }
            
            // Check for boss collision
            if (boss != null && i < bullets.size() && bullet.collidesWith(boss)) {
                bullets.remove(i);
                bossHealth--;
                score += 5;
                
                if (bossHealth <= 0) {
                    // Boss defeated
                    createExplosion(boss.x + BOSS_SIZE/2, boss.y + BOSS_SIZE/2, BOSS_COLOR);
                    boss = null;
                    playerWins = true;
                    endGame();
                }
            }
        }
    }
    
    private void spawnEnemies() {
        if (random.nextInt(100) < ENEMY_SPAWN_RATE) {
            int side = random.nextInt(4);
            int x = 0, y = 0;
            
            switch (side) {
                case 0: // Top
                    x = random.nextInt(screenWidth - ENEMY_SIZE);
                    y = -ENEMY_SIZE;
                    break;
                case 1: // Right
                    x = screenWidth;
                    y = random.nextInt(screenHeight - ENEMY_SIZE);
                    break;
                case 2: // Bottom
                    x = random.nextInt(screenWidth - ENEMY_SIZE);
                    y = screenHeight;
                    break;
                case 3: // Left
                    x = -ENEMY_SIZE;
                    y = random.nextInt(screenHeight - ENEMY_SIZE);
                    break;
            }
            
            enemies.add(new Enemy(x, y, ENEMY_SIZE, ENEMY_SIZE));
        }
        
        // Spawn boss after killing enough enemies
        if (enemiesKilled >= BOSS_SPAWN_THRESHOLD && boss == null) {
            boss = new Boss(screenWidth / 2 - BOSS_SIZE / 2, 
                          screenHeight / 2 - BOSS_SIZE / 2, 
                          BOSS_SIZE, BOSS_SIZE);
        }
    }
    
    private void updateEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.moveTowards(player.x + PLAYER_SIZE/2, player.y + PLAYER_SIZE/2, ENEMY_SPEED);
            
            if (enemy.intersects(player)) {
                playerHealth -= 10;
                enemies.remove(i);
                createExplosion(enemy.x + ENEMY_SIZE/2, enemy.y + ENEMY_SIZE/2, ENEMY_COLOR);
                
                if (playerHealth <= 0) {
                    gameOver = true;
                    endGame();
                }
            }
        }
    }
    
    private void updateBoss() {
        if (boss != null) {
            boss.moveTowards(player.x + PLAYER_SIZE/2, player.y + PLAYER_SIZE/2, BOSS_SPEED);
            
            if (boss.intersects(player)) {
                playerHealth -= 20;
                
                if (playerHealth <= 0) {
                    gameOver = true;
                    endGame();
                }
            }
        }
    }
    
    private void updateParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
            }
        }
    }
    
    private void createExplosion(int x, int y, Color color) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x, y, color, random));
        }
    }
    
    private void increaseScreenSize() {
        screenWidth += 15;
        screenHeight += 10;
        screenWidth = Math.min(screenWidth, MAX_WIDTH);
        screenHeight = Math.min(screenHeight, MAX_HEIGHT);
        updateScreenSize();
    }
    
    private void checkWinCondition() {
        if (screenWidth >= MAX_WIDTH && screenHeight >= MAX_HEIGHT) {
            playerWins = true;
            endGame();
        }
    }
    
    private void endGame() {
        gameRunning = false;
        gameTimer.stop();
        shootTimer.stop();
        
        if (playerWins) {
            winScreenTimeLeft = 10;
            winScreenTimer.start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keys[keyCode] = true;

        if (keyCode == KeyEvent.VK_P && gameRunning) {
            gamePaused = !gamePaused;
            if (gamePaused) {
                shootTimer.stop();
            } else {
                shootTimer.start();
            }
        }

        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (gameRunning) {
                gamePaused = !gamePaused;
                if (gamePaused) {
                    shootTimer.stop();
                } else {
                    shootTimer.start();
                }
            }
        }
        
        // Cheat codes
        if (keyCode == KeyEvent.VK_H) {
            PLAYER_MAX_HEALTH = 1000;
            playerHealth = PLAYER_MAX_HEALTH;
            autoShootDelay = 5;
            shootTimer.stop();
            shootTimer.setDelay(autoShootDelay);
            shootTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keys[keyCode] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (playerWins || gameOver) {
            resetGame();
            startGame();
        } else if (!gameRunning) {
            if (startButton.contains(e.getPoint())) {
                startGame();
            } else if (exitButton.contains(e.getPoint())) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fill The Screen");
        FillTheScreen game = new FillTheScreen(frame);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
}