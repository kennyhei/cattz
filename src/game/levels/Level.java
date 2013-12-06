package game.levels;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Matrix3f;
import com.jme3.scene.Node;
import game.Main;
import game.models.Block;
import java.util.List;

public abstract class Level {

    protected final int TEXTURE_ID = 6;
    protected Node terrainNode;
    protected int[][] blockCheckList;

    public Level() {
        registerBlocks();
        createWorld();
    }

    private void registerBlocks() {
        BlockManager.register(Block_Brick.class,
                new BlockSkin(new BlockSkin_TextureLocation[]{
            new BlockSkin_TextureLocation(TEXTURE_ID, 0),
            new BlockSkin_TextureLocation(TEXTURE_ID, 0),
            new BlockSkin_TextureLocation(TEXTURE_ID, 0),
            new BlockSkin_TextureLocation(TEXTURE_ID, 0),
            new BlockSkin_TextureLocation(TEXTURE_ID, 0),
            new BlockSkin_TextureLocation(TEXTURE_ID, 0)
        }, false));
    }

    private final void createWorld() {
        BlockTerrainControl blockTerrain =
                new BlockTerrainControl(
                CubesTestAssets.getSettings(Main.getApp()), new Vector3Int(1, 1, 1));
        // create floor
        for (int x = 0; x < 6; x++) {
            for (int z = 0; z < 7; z++) {
                blockTerrain.setBlock(x, 0, z, Block_Brick.class);
            }
        }

        // create wall
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                blockTerrain.setBlock(x, y, 0, Block_Brick.class);
            }
        }

        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.addControl(new RigidBodyControl(0));

        PhysicsSpace physicsSpace = Main.getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        physicsSpace.addAll(terrainNode);
    }

    abstract List<Block> getBlocks();

    abstract List<Block> getSolution();

    public Node getTerrain() {
        return terrainNode;
    }

    public List<Block> getPuzzlePieces() {
        return getBlocks();
    }

    public boolean checkBlocks() {
        List<Block> checkPieces = getSolution();
        List<Block> puzzlePieces = getPuzzlePieces();

        for (int i = 0; i < puzzlePieces.size(); i++) {
            Block block = puzzlePieces.get(i);
            Block correctBlock = checkPieces.get(i);

            if (!checkBlockPosition(block, correctBlock)) {
                return false;
            }

            if (!checkBlockRotation(block, correctBlock)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkBlockPosition(Block toCheck, Block correct) {
//        System.out.println(toCheck.getBlockGeometry().getLocalTranslation());
        if (Math.round(toCheck.getBlockGeometry().getLocalTranslation().x * 10)
                != Math.round(correct.getBlockGeometry().getLocalTranslation().x * 10)
                || Math.round(toCheck.getBlockGeometry().getLocalTranslation().y * 10)
                != Math.round(correct.getBlockGeometry().getLocalTranslation().y * 10)
                || Math.round(toCheck.getBlockGeometry().getLocalTranslation().z * 10)
                != Math.round(correct.getBlockGeometry().getLocalTranslation().z * 10)) {
            return false;
        } else {
            System.out.println("Block position correct!");
            return true;
        }
    }

    private boolean checkBlockRotation(Block toCheck, Block correct) {

        // TODO: this matrix needs to have only positive values of either 0.0 or 1.0
        // (right now float-type inaccuracies and negative values crop up)
        Matrix3f matrixToCheck = toCheck.getBlockGeometry().getLocalRotation().toRotationMatrix();
//        System.out.println(matrixToCheck);
        if (matrixToCheck.equals(correct.getBlockGeometry().getLocalRotation().toRotationMatrix())) {
            System.out.println("Block rotation correct!");
            return true;
        } else {
            return false;
        }
    }
}
