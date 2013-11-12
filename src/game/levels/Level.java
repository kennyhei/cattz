package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.scene.Node;
import game.Main;

public abstract class Level {

    protected Node terrainNode;
    protected Main app;

    public Level(Main app) {
        this.app = app;
        registerBlocks();
    }

    private void registerBlocks() {
        BlockManager.register(Block_Brick.class, new BlockSkin(new BlockSkin_TextureLocation[]{
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0)
        }, false));
    }

    abstract void createWorld();

    public Node getTerrain() {
        return terrainNode;
    }
}
