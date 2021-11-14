import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena {
    private boolean dead = false;
    private int width;
    private int height;
    private int lives = 3;
    private Hero hero;
    private List<Wall> walls;
    private List<Coin> coins;
    private List<Monster> monsters;

    public Arena(int width, int height) {
        this.width = width;
        this.height = height;
        this.hero = new Hero(new Position(10, 10));
        this.walls = createWalls();
        this.coins = createCoins();
        this.monsters = createMonsters();
    }

    private List<Monster> createMonsters() {
        Random random = new Random();
        ArrayList<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            monsters.add(new Monster(new Position(random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1)));
        return monsters;
    }

    private List<Coin> createCoins() {
        Random random = new Random();
        ArrayList<Coin> coins = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            coins.add(new Coin(new Position(random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1)));
        return coins;
    }

    private List<Wall> createWalls() {
        List<Wall> walls = new ArrayList<>();
        for (int c = 0; c < width; c++) {
            walls.add(new Wall(new Position(c, 0)));
            walls.add(new Wall(new Position(c, height - 1)));
        }
        for (int r = 1; r < height - 1; r++) {
            walls.add(new Wall(new Position(0, r)));
            walls.add(new Wall(new Position(width - 1, r)));
        }
        return walls;
    }

    private boolean canElementMove(Position position) {
        for (Wall wall : walls)
            if (wall.getPosition().equals(position)) return false;
        return true;
    }

    public void moveHero(Position position) {
        if(canElementMove(position)) hero.setPosition(position);
        retrieveCoins();
    }

    public void moveMonsters() {
        for (Monster monster : monsters){
            Position pos = monster.move();
            if (canElementMove(pos)) monster.setPosition(pos);
        }
    }

    private void retrieveCoins(){
        for (Coin coin : coins){
            if (coin.getPosition().equals(hero.getPosition())){
                coins.remove(coin);
                break;
            }
        }
    }

    public void verifyMonsterCollisions() {
        for (Monster monster : monsters){
            if(monster.getPosition().equals(hero.getPosition())){
                lives--;
                System.out.println("Lives = " + lives);
                if(lives <= 0) dead = true;
            }
        }
    }

    public boolean getDead(){
        return dead;
    }

    public void processKey(KeyStroke key, Screen screen) throws IOException {
        if(key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') screen.close();
        if(key.getKeyType() == KeyType.ArrowLeft) moveHero(hero.moveLeft());
        if(key.getKeyType() == KeyType.ArrowRight) moveHero(hero.moveRight());
        if(key.getKeyType() == KeyType.ArrowUp) moveHero(hero.moveUp());
        if(key.getKeyType() == KeyType.ArrowDown) moveHero(hero.moveDown());
        moveMonsters();
        verifyMonsterCollisions();
    }

    public void draw(TextGraphics graphics) {
        hero.draw(graphics);
        for (Wall wall : walls)
            wall.draw(graphics);
        for (Coin coin : coins)
            coin.draw(graphics);
        for (Monster monster : monsters)
            monster.draw(graphics);
    }
}
