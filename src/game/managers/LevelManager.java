package game.managers;

import game.levels.Easy;
import game.levels.Hard;
import game.levels.Level;
import game.levels.Medium;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class LevelManager {

    private SortedMap<Integer, String> levelOrder;
    private Map<String, Level> levels;
    private Map<Integer, Boolean> enabledLevels;
    private int currentLevelOrder;

    public LevelManager() {
        this.levels = new TreeMap<String, Level>();
        this.levelOrder = new TreeMap<Integer, String>();
        this.enabledLevels = new TreeMap<Integer, Boolean>();

        // could load game progress from disk here
        addLevel(1, "Baby steps", new Easy(), true);
        addLevel(2, "Didn't see this one coming!", new Medium(), false);
        addLevel(3, "Oh no, more levels!", new Hard(), false);

        this.currentLevelOrder = 2;
    }

    public Level getCurrentLevel() {
        return levels.get(levelOrder.get(currentLevelOrder));
    }
    
    public void currentLevelCleared() {
        currentLevelOrder++;
        enabledLevels.put(currentLevelOrder, Boolean.TRUE);
    }

    public boolean isEnabled(int levelIndex) {
        return enabledLevels.containsKey(levelIndex) && enabledLevels.get(levelIndex);
    }

    public SortedMap<Integer, String> getLevelOrdering() {
        return levelOrder;
    }

    private void addLevel(int order, String levelName, Level level, boolean enabled) {
        levels.put(levelName, level);
        levelOrder.put(order, levelName);
        enabledLevels.put(order, enabled);
    }

    public void setActiveLevel(int levelIndex) {
        if (!enabledLevels.keySet().contains(levelIndex)) {
            return;
        }

        currentLevelOrder = levelIndex;
    }
}
