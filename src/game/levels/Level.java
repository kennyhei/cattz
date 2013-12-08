package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Matrix3f;
import com.jme3.scene.Node;
import game.Main;
import game.models.Block;
import game.models.blockclasses.BlockRegular;
import game.models.blockclasses.BlockSolution;
import java.util.List;

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

    public boolean checkBlocks() {
        List<Block> checkPieces = getSolution();
        List<Block> puzzlePieces = getPuzzlePieces();

        // lets not assume same order, but same color
        for (Block puzzlePiece : puzzlePieces) {
            for (Block solutionPiece : checkPieces) {
                if (!puzzlePiece.getColor().equals(solutionPiece.getColor())) {
                    continue;
                }

                if (!checkBlockPosition(puzzlePiece, solutionPiece)) {
                    return false;
                }

                if (!checkBlockRotation(puzzlePiece, solutionPiece)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean checkBlockPosition(Block toCheck, Block correct) {
//        System.out.println(toCheck.getBlockGeometry().getLocalTranslation());
        if (Math.round(toCheck.getPivot().getLocalTranslation().x * 10)
                != Math.round(correct.getPivot().getLocalTranslation().x * 10)
                || Math.round(toCheck.getPivot().getLocalTranslation().y * 10)
                != Math.round(correct.getPivot().getLocalTranslation().y * 10)
                || Math.round(toCheck.getPivot().getLocalTranslation().z * 10)
                != Math.round(correct.getPivot().getLocalTranslation().z * 10)) {
            return false;
        } else {
            System.out.println("Block position correct!");
            return true;
        }
    }

    private boolean checkBlockRotation(Block toCheck, Block correct) {
        if(true) {
            return true;
        }

        // TODO: this matrix needs to have only positive values of either 0.0 or 1.0
        // (right now float-type inaccuracies and negative values crop up)
        Matrix3f matrixToCheck = toCheck.getPivot().getLocalRotation().toRotationMatrix();
//        System.out.println(matrixToCheck);
        if (matrixToCheck.equals(correct.getPivot().getLocalRotation().toRotationMatrix())) {
            System.out.println("Block rotation correct!");
            return true;
        } else {
            return false;
        }
    }
}
