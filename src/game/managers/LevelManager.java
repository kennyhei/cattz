package game.managers;

import game.levels.Easy;
import game.levels.Hard;
import game.levels.Level;
import game.levels.Medium;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

public class LevelManager {

    private SortedMap<Integer, String> levelOrder;
    private Map<String, Class<? extends Level>> levels;
    private Map<Integer, Boolean> enabledLevels;
    private Level currentLevel;
    private int currentLevelOrder;

    public LevelManager() {
        this.levels = new TreeMap<String, Class<? extends Level>>();
        this.levelOrder = new TreeMap<Integer, String>();
        this.enabledLevels = new TreeMap<Integer, Boolean>();

        // could load game progress from disk here
        addLevel(1, "Baby steps", Easy.class, true);
        addLevel(2, "Didn't see this one coming!", Medium.class, false);
        addLevel(3, "Oh no, more levels!", Hard.class, false);

        this.currentLevelOrder = 1;
    }

    public Level getCurrentLevel() {
        if (currentLevel == null) {
            try {
                currentLevel = levels.get(levelOrder.get(currentLevelOrder)).newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(LevelManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(LevelManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        return currentLevel;
    }

    public void currentLevelCleared() {
        currentLevel = null;
        currentLevelOrder++;
        enabledLevels.put(currentLevelOrder, Boolean.TRUE);
    }

    public boolean isEnabled(int levelIndex) {
        return enabledLevels.containsKey(levelIndex) && enabledLevels.get(levelIndex);
    }

    public SortedMap<Integer, String> getLevelOrdering() {
        return levelOrder;
    }

    private void addLevel(int order, String levelName, Class<? extends Level> level, boolean enabled) {
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
