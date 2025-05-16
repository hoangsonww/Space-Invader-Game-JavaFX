package com.example.spaceshootergamejavafx;

import java.util.List;
import java.util.Random;

/**
 * AI controller for the player in Space Shooter game.
 * This class handles automatic movement and shooting for the player.
 */
public class AIController {
    /** The player being controlled by the AI */
    private final Player player;
    
    /** List of game objects to track enemies and bullets */
    private final List<GameObject> gameObjects;
    
    /** List for adding new objects like bullets */
    private final List<GameObject> newObjects;
    
    /** Random number generator for decision making */
    private final Random random = new Random();
    
    /** Cooldown timer for shooting */
    private long lastShotTime = 0;
    
    /** Minimum time between shots in milliseconds */
    private static final long SHOT_COOLDOWN = 500;
    
    /** Chance of shooting when an enemy is detected (0-100) */
    private static final int SHOOT_CHANCE = 70;
    
    /**
     * Creates a new AI controller for the player.
     *
     * @param player The player to control
     * @param gameObjects List of all game objects
     * @param newObjects List to add new objects to
     */
    public AIController(Player player, List<GameObject> gameObjects, List<GameObject> newObjects) {
        this.player = player;
        this.gameObjects = gameObjects;
        this.newObjects = newObjects;
    }
    
    /**
     * Updates the AI's decision making and controls the player.
     * This method should be called once per game loop.
     */
    public void update() {
        // Reset movement flags
        player.setMoveLeft(false);
        player.setMoveRight(false);
        player.setMoveForward(false);
        player.setMoveBackward(false);
        
        // Find closest enemy
        GameObject closestEnemy = findClosestEnemy();
        
        // If there's an enemy, try to move toward it horizontally
        if (closestEnemy != null) {
            // Move horizontally to align with enemy
            if (closestEnemy.getX() < player.getX() - 10) {
                player.setMoveLeft(true);
            } else if (closestEnemy.getX() > player.getX() + 10) {
                player.setMoveRight(true);
            }
            
            // Occasionally move vertically to dodge
            if (random.nextInt(100) < 5) {
                if (random.nextBoolean() && player.getY() > 100) {
                    player.setMoveForward(true);
                } else if (player.getY() < SpaceShooter.HEIGHT - 100) {
                    player.setMoveBackward(true);
                }
            }
            
            // Try to shoot if aligned with enemy
            if (Math.abs(closestEnemy.getX() - player.getX()) < 30) {
                tryToShoot();
            }
        } else {
            // If no enemies, patrol horizontally
            patrolMovement();
        }
        
        // Avoid hitting the edges of the screen
        avoidEdges();
        
        // Occasionally shoot even if not aligned
        if (random.nextInt(100) < 5) {
            tryToShoot();
        }
    }
    
    /**
     * Tries to shoot if cooldown has elapsed.
     */
    private void tryToShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOT_COOLDOWN && random.nextInt(100) < SHOOT_CHANCE) {
            player.shoot(newObjects);
            lastShotTime = currentTime;
        }
    }
    
    /**
     * Finds the closest enemy to the player.
     *
     * @return The closest enemy, or null if no enemies exist
     */
    private GameObject findClosestEnemy() {
        GameObject closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (GameObject obj : gameObjects) {
            if (obj instanceof Enemy) {
                double distance = calculateDistance(player, obj);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = obj;
                }
            }
        }
        
        return closest;
    }
    
    /**
     * Calculates the distance between two game objects.
     *
     * @param obj1 First game object
     * @param obj2 Second game object
     * @return The distance between the objects
     */
    private double calculateDistance(GameObject obj1, GameObject obj2) {
        double dx = obj1.getX() - obj2.getX();
        double dy = obj1.getY() - obj2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Implements patrol movement when no enemies are detected.
     */
    private void patrolMovement() {
        // Simple left-right patrol
        if (player.getX() < 100) {
            player.setMoveRight(true);
        } else if (player.getX() > SpaceShooter.WIDTH - 100) {
            player.setMoveLeft(true);
        } else if (random.nextBoolean()) {
            player.setMoveRight(true);
        } else {
            player.setMoveLeft(true);
        }
    }
    
    /**
     * Prevents the player from hitting the edges of the screen.
     */
    private void avoidEdges() {
        // Avoid left edge
        if (player.getX() < 50) {
            player.setMoveLeft(false);
            player.setMoveRight(true);
        }
        
        // Avoid right edge
        if (player.getX() > SpaceShooter.WIDTH - 50) {
            player.setMoveRight(false);
            player.setMoveLeft(true);
        }
        
        // Avoid top edge
        if (player.getY() < 50) {
            player.setMoveForward(false);
            player.setMoveBackward(true);
        }
        
        // Avoid bottom edge
        if (player.getY() > SpaceShooter.HEIGHT - 50) {
            player.setMoveBackward(false);
            player.setMoveForward(true);
        }
    }
}
