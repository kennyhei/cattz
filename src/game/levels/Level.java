package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.Main;
import game.models.Block;
import java.util.List;

public abstract class Level {
    public static final float BLOCK_SIDE_WIDTH = 1.5f;
    
    protected Node terrainNode;
    protected Main app;
    protected AssetManager assetManager;
    protected List<Block> puzzlePieces;
    protected int[][] blockCheckList;

    public Level(Main app) {
        this.app = app;
        this.assetManager = this.app.getAssetManager();
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

    public List<Block> getPuzzlePieces() {
        return this.puzzlePieces;
    }

    public boolean checkBlocks() {
        for (int i = 0; i < puzzlePieces.size(); i++) {
            Block block = puzzlePieces.get(i);
            
            if (!checkBlock(block.getBlockGeometry().getLocalTranslation(), i)) {
                return false;
            }
        }
        
        return true;
    }

    public boolean checkBlock(Vector3f position, int index) {
        if (blockCheckList[index][0] != ((int) (position.x * 10))
                || blockCheckList[index][1] != ((int) (position.y * 10))
                || blockCheckList[index][2] != ((int) (position.z * 10))) {
            return false;
        } else {
            return true;
        }
    }
}
