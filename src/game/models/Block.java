package game.models;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import game.controllers.BlockControl;

// Kubus block
public class Block {

    public static float[][] locations = {{57, 13, 104}, {21, 13, 101}, {84, 13, 148},
                                          {174, 13, 116}, {252, 13, 128}, {538, 13, 19},
                                          {551, 13, 81}, {580, 13, 127}, {576, 13, 185},
                                          {581, 13, 228}, {559, 13, 519}, {542, 13, 457},
                                          {301, 13, 563}, {258, 13, 563}, {179, 13, 518},
                                          {102, 13, 562}, {36, 13, 564}, {128, 13, 349},
                                          {47, 13, 308}, {467, 13, 443}};

    private Box blockBox;
    private Geometry blockGeometry;
    private Material blockMaterial;
    private GhostControl physics;

    public Block(AssetManager assetManager, ColorRGBA color, Vector3f location, float[] size) {

        // Create kubus block
        this.blockBox = new Box(size[0], size[1], size[2]);
        this.blockGeometry = new Geometry("Block", blockBox);
        this.blockGeometry.setLocalTranslation(location);

        this.blockMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.blockMaterial.setColor("Color", color);
        this.blockGeometry.setMaterial(blockMaterial);

        this.physics = new GhostControl(new BoxCollisionShape(new Vector3f(2f, 4f, 1f)));
        this.blockGeometry.addControl(physics);

        this.blockGeometry.addControl(new BlockControl(color));
    }

    public Geometry getBlockGeometry() {
        return blockGeometry;
    }

    public GhostControl getPhysics() {
        return physics;
    }

    public void setLocation(Vector3f newLocation) {
        this.blockGeometry.setLocalTranslation(newLocation);
    }
}
