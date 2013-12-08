package game.levels;

import com.cubes.BlockTerrainControl;
import com.jme3.math.ColorRGBA;
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

        // 3x2 puzzle piece
        puzzlePieces.add(new Block(ColorRGBA.Pink,
                corner.clone()));
        
        puzzlePieces.add(new Block(ColorRGBA.Red,
                corner.clone().setX(9.5f)));
    }

    private void createCheckPieces() {
        checkPieces = new ArrayList<Block>();

        Vector3f corner = new Vector3f(4.5f, 4.5f, 10.5f);

        // 3x2 puzzle piece
        checkPieces.add(new Block(ColorRGBA.Pink,
                corner.clone()));
        
        
        checkPieces.add(new Block(ColorRGBA.Red,
                corner.clone()));
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
        return "Textures/maze-easy.png";
    }

    @Override
    public void addTerrainBlocks(BlockTerrainControl control) {
        // create floor
        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 7; z++) {
                control.setBlock(x, 0, z, BlockRegular.class);
            }
        }

        // create wall
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
