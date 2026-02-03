import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ShootingGame extends JPanel implements ActionListener, KeyListener {

    // Game constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_SIZE = 50;
    private static final int BULLET_SIZE = 10;
    private static final int ENEMY_SIZE = 40;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final int ENEMY_SPEED = 3;

    // Game state
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;

    // Entities
    private Rectangle player;
    private ArrayList<Rectangle> bullets;
    private ArrayList<Rectangle> enemies;
    private Random random;

    // Input state
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean canShoot = true;

    public ShootingGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Rectangle(WIDTH / 2 - PLAYER_SIZE / 2, HEIGHT - 100, PLAYER_SIZE, PLAYER_SIZE);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        random = new Random();

        // Game loop: 60 FPS
        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updatePlayer();
            updateBullets();
            updateEnemies();
            checkCollisions();
            repaint();
        }
    }

    private void updatePlayer() {
        if (leftPressed && player.x > 0) {
            player.x -= PLAYER_SPEED;
        }
        if (rightPressed && player.x < WIDTH - PLAYER_SIZE) {
            player.x += PLAYER_SPEED;
        }
        // Simple shooting cooldown logic could be added here,
        // for now space spawns one bullet per press if handled in keyPressed,
        // or continuous stream if handled here. Let's do key press for single shot.
    }

    private void updateBullets() {
        Iterator<Rectangle> it = bullets.iterator();
        while (it.hasNext()) {
            Rectangle bullet = it.next();
            bullet.y -= BULLET_SPEED;
            if (bullet.y + BULLET_SIZE < 0) {
                it.remove();
            }
        }
    }

    private void updateEnemies() {
        // Spawn enemies randomly
        if (random.nextInt(100) < 2) { // 2% chance per frame
            enemies.add(new Rectangle(random.nextInt(WIDTH - ENEMY_SIZE), 0, ENEMY_SIZE, ENEMY_SIZE));
        }

        Iterator<Rectangle> it = enemies.iterator();
        while (it.hasNext()) {
            Rectangle enemy = it.next();
            enemy.y += ENEMY_SPEED;
            if (enemy.y > HEIGHT) {
                it.remove(); // Enemy passed player, maybe decrease score?
            }
        }
    }

    private void checkCollisions() {
        // Bullet vs Enemy
        Iterator<Rectangle> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Rectangle bullet = bulletIt.next();
            Iterator<Rectangle> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Rectangle enemy = enemyIt.next();
                if (bullet.intersects(enemy)) {
                    bulletIt.remove();
                    enemyIt.remove();
                    score += 10;
                    break; // Bullet hits one enemy
                }
            }
        }

        // Enemy vs Player
        for (Rectangle enemy : enemies) {
            if (enemy.intersects(player)) {
                gameOver = true;
                timer.stop();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String msg = "Game Over! Score: " + score;
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(msg, (WIDTH - metrics.stringWidth(msg)) / 2, HEIGHT / 2);
            return;
        }

        // Draw Player
        g.setColor(Color.BLUE);
        g.fillRect(player.x, player.y, player.width, player.height);

        // Draw Bullets
        g.setColor(Color.YELLOW);
        for (Rectangle bullet : bullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        // Draw Enemies
        g.setColor(Color.RED);
        for (Rectangle enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }

        // Draw Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 25);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (key == KeyEvent.VK_RIGHT)
            rightPressed = true;
        if (key == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                bullets.add(new Rectangle(player.x + PLAYER_SIZE / 2 - BULLET_SIZE / 2, player.y, BULLET_SIZE,
                        BULLET_SIZE));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (key == KeyEvent.VK_RIGHT)
            rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        ShootingGame game = new ShootingGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
