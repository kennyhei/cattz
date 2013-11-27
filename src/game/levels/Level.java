package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.Main;
import java.util.List;

public abstract class Level {

    protected Node terrainNode;
    protected Main app;
    protected AssetManager assetManager;
    protected Node puzzlePieces;
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
    
    public Node getPuzzlePieces() {
        return this.puzzlePieces;
    }
    
    public boolean checkBlocks() {
        List<Spatial> pieces = puzzlePieces.getChildren();
        for (int i = 0; i<pieces.size(); i++) {
            if (!checkBlock(pieces.get(i).getLocalTranslation(), i)) {
                return false;
            }
        }
        return true;
    }
    public boolean checkBlock(Vector3f position, int index) {
            if (blockCheckList[index][0] != ((int) (position.x*10)) || 
                    blockCheckList[index][1] != ((int) (position.y*10)) || 
                    blockCheckList[index][2] != ((int) (position.z*10)) ) {
                return false;
            }
            else
                return true;
    }
}
