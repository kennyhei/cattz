package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import game.Main;
import game.models.Block;
import game.models.blockclasses.BlockRegular;
import game.models.blockclasses.BlockSolution;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Level {

    protected Node terrainNode;
    protected int[][] blockCheckList;

    public Level() {
        registerBlocks();
        createWorld();
    }

    private void registerBlocks() {
        int solutionColumn = 3;
        int solutionRow = 1;
        BlockManager.register(BlockSolution.class,
                new BlockSkin(new BlockSkin_TextureLocation[]{
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow)
        }, false));

        solutionColumn = 6;
        solutionRow = 0;
        BlockManager.register(BlockRegular.class,
                new BlockSkin(new BlockSkin_TextureLocation[]{
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow),
            new BlockSkin_TextureLocation(solutionColumn, solutionRow)
        }, false));
    }

    private final void createWorld() {
        CubesSettings settings = CubesTestAssets.getSettings(Main.getApp());
        settings.setDefaultBlockMaterial("Textures/cubes-terrain.png");

        BlockTerrainControl blockTerrain =
                new BlockTerrainControl(
                settings, new Vector3Int(1, 1, 1));

        addTerrainBlocks(blockTerrain);

        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.addControl(new RigidBodyControl(0));

        PhysicsSpace physicsSpace = Main.getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        physicsSpace.addAll(terrainNode);
    }

    public abstract void addTerrainBlocks(BlockTerrainControl control);

    public abstract List<Block> getBlocks();

    public abstract List<Block> getSolution();

    public abstract String getLevelHeightMap();

    public Node getTerrain() {
        return terrainNode;
    }

    public List<Block> getPuzzlePieces() {
        return getBlocks();
    }

    public boolean isFinished() {
        Map<Block, Boolean> piecesCovered = new HashMap<Block, Boolean>();

        for (Block solutionPiece : getSolution()) {
            solutionPiece.getPivot().updateGeometricState();
            solutionPiece.getPivot().updateModelBound();

            for (Block puzzlePiece : getPuzzlePieces()) {
                puzzlePiece.getPivot().updateGeometricState();
                puzzlePiece.getPivot().updateModelBound();

                BoundingBox solutionBox = (BoundingBox) solutionPiece.getWorldBound();
                BoundingBox puzzleBox = (BoundingBox) puzzlePiece.getWorldBound();

                if (solutionBox.getCenter().distance(puzzleBox.getCenter()) > 1f) {
                    continue;
                }

                if (Math.abs(solutionBox.getXExtent() - puzzleBox.getXExtent()) > 0.01) {
                    continue;
                }

                if (Math.abs(solutionBox.getYExtent() - puzzleBox.getYExtent()) > 0.01) {
                    continue;
                }

                if (Math.abs(solutionBox.getZExtent() - puzzleBox.getZExtent()) > 0.01) {
                    continue;
                }

                piecesCovered.put(solutionPiece, Boolean.TRUE);
            }
        }

        return piecesCovered.size() >= getSolution().size();
    }
}
