package game.levels;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import game.models.Block;
import java.util.ArrayList;
import java.util.List;

public class LevelOne extends Level {

    private List<Block> puzzlePieces;
    private List<Block> checkPieces;

    public LevelOne() {
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
                corner.clone()));
    }

    private void createCheckPieces() {
        checkPieces = new ArrayList<Block>();

        Block checkPiece = new Block(
                ColorRGBA.randomColor(),
                new Vector3f(10.5f, 13.6f, 15f));
        checkPiece.getBlockGeometry().setLocalRotation(new Matrix3f(0f, 1f, 0f,
                0f, 0f, 1f,
                1f, 0f, 0f));
        checkPieces.add(checkPiece);

        checkPiece = new Block(ColorRGBA.randomColor(),
                new Vector3f(4f, 9f, 22f));
        checkPiece.getBlockGeometry().setLocalRotation(new Matrix3f(1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f));
        checkPieces.add(checkPiece);
    }

    @Override
    public List<Block> getBlocks() {
        return puzzlePieces;
    }

    @Override
    public List<Block> getSolution() {
        return checkPieces;
    }
}
