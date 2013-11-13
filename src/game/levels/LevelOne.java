package game.levels;

import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import game.Main;

public class LevelOne extends Level {

    public LevelOne(Main app) {
        super(app);
        createWorld();
    }

    @Override
    void createWorld() {
        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this.app), new Vector3Int(1, 1, 1));
        blockTerrain.setBlockArea(new Vector3Int(0, 2, 1), new Vector3Int(6, 6, 1), Block_Brick.class);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 1), new Vector3Int(6, 1, 7), Block_Brick.class);

        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
    }
}
