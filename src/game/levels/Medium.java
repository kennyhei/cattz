package game.levels;

import com.cubes.BlockTerrainControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import game.models.Block;
import game.models.blockclasses.BlockRegular;
import game.models.blockclasses.BlockSolution;
import java.util.ArrayList;
import java.util.List;

public class Medium extends Level {

    private List<Block> puzzlePieces;
    private List<Block> checkPieces;

    public Medium() {
        init();
    }

    private void init() {
        createPuzzlePieces();
        createCheckPieces();
    }

    private void createPuzzlePieces() {
        this.puzzlePieces = new ArrayList<Block>();

        Vector3f corner = new Vector3f(4.5f, 4.5f, 16.5f);

        puzzlePieces.add(new Block(ColorRGBA.Pink,
                corner.clone()));

        puzzlePieces.add(new Block(ColorRGBA.Red,
                corner.clone().add(3f, 0f, 0f)));
    }

    private void createCheckPieces() {
        checkPieces = new ArrayList<Block>();

        Block one = new Block(ColorRGBA.Pink, new Vector3f(4.5f, 4.5f, 19.5f));
        one.setRotation(new Quaternion(0.0f, -0.9999999f, 0.0f, 0.0f));

        Block two = new Block(ColorRGBA.Red, new Vector3f(13.5f, 10.5f, 4.5f));
        two.setRotation(new Quaternion(0.7071067f, 0.0f, 0.0f, 0.7071067f));

        checkPieces.add(one);
        checkPieces.add(two);
    }

    @Override
    public List<Block> getBlocks() {
        return puzzlePieces;
    }

    @Override
    public List<Block> getSolution() {
        return checkPieces;
    }

    @Override
    public String getLevelHeightMap() {
        return "Textures/maze-medium.png";
    }

    @Override
    public void addTerrainBlocks(BlockTerrainControl control) {
        // create floor
        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 7; z++) {
                control.setBlock(x, 0, z, BlockRegular.class);
            }
        }

        // Create wall
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                control.setBlock(x, y, 0, BlockRegular.class);
            }
        }

        for (int i = 0; i < 3; i++) {
            control.setBlock(i, 0, 5, BlockSolution.class);
            control.setBlock(i, 0, 6, BlockSolution.class);
        }

        for (int i = 0; i < 3; i++) {
            control.setBlock(3 + i, 2, 0, BlockSolution.class);
            control.setBlock(3 + i, 3, 0, BlockSolution.class);
        }
    }
}
