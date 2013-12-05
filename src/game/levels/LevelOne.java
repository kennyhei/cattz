package game.levels;

import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.Main;
import static game.levels.Level.BLOCK_SIDE_WIDTH;
import game.models.Block;
import java.util.ArrayList;

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


        this.puzzlePieces = new ArrayList<Block>(); // ("Controllable Blocks");
        this.checkPieces = new ArrayList<Block>(); // correct blocks
   
        Block checkPiece = new Block(assetManager,
                ColorRGBA.randomColor(),
                new Vector3f(10.5f, 13.6f, 15f),
                new float[]{3 * BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH, 2 * BLOCK_SIDE_WIDTH});
        checkPiece.getBlockGeometry().setLocalRotation(new Matrix3f(0f, 1f, 0f, 
                                                                    0f, 0f, 1f,
                                                                    1f, 0f, 0f));
        checkPieces.add(checkPiece);
        
        checkPiece = new Block(assetManager,
                ColorRGBA.randomColor(),
                new Vector3f(4f, 9f, 22f),
                new float[]{BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH});  
        checkPiece.getBlockGeometry().setLocalRotation(new Matrix3f(1f, 0f, 0f, 
                                                                    0f, 1f, 0f,
                                                                    0f, 0f, 1f));
        checkPieces.add(checkPiece);

        // 3x2 puzzle piece
        Block block = new Block(assetManager,
                ColorRGBA.randomColor(),
                new Vector3f(4.5f, 7.6f, 21f),
                new float[]{3 * BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH, 2 * BLOCK_SIDE_WIDTH});
        puzzlePieces.add(block);

        block = new Block(assetManager,
                ColorRGBA.randomColor(),
                new Vector3f(4f, 15f, 10f),
                new float[]{BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH, BLOCK_SIDE_WIDTH});
        puzzlePieces.add(block);


        this.blockCheckList = new int[][]{
            {105, 136, 150, 5},
            {105, 136, 150, 5}
        };
    }
}
