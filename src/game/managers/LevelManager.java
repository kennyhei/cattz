package game.managers;

import game.Main;
import game.levels.Level;
import game.levels.LevelOne;
import java.util.HashMap;

public class LevelManager {

    HashMap<Integer, Level> levels;

    public LevelManager(Main app) {
        this.levels = new HashMap<Integer, Level>();

        levels.put(0, new LevelOne(app));
    }

    public Level getLevel(int levelId) {
        return levels.get(levelId);
    }

}
