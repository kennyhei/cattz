package game.models;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import game.Main;
import game.controllers.BlockControl;

// Kubus block
public class Block {

    public static final float BLOCK_SIDE_WIDTH = 1.5f;
    public static float[][] locations = {{57, 13, 104}, {21, 13, 101}, {84, 13, 148},
        {174, 13, 116}, {252, 13, 128}, {338, 13, 19},
        {351, 13, 81}, {380, 13, 127}, {376, 13, 185},
        {381, 13, 228}, {359, 13, 519}, {342, 13, 257},
        {301, 13, 363}, {258, 13, 363}, {179, 13, 318},
        {102, 13, 362}, {36, 13, 364}, {128, 13, 349},
        {47, 13, 308}, {267, 13, 243}};
    private Node pivot;
    private Box box;
    private Geometry blockGeometry;
    private Material blockMaterial;
    private GhostControl physics;
    private ColorRGBA color;

    public Block(ColorRGBA color, Vector3f location) {
        this(Main.getApp().getAssetManager(),
                color,
                location,
                new float[]{
            3 * BLOCK_SIDE_WIDTH,
            BLOCK_SIDE_WIDTH,
            2 * BLOCK_SIDE_WIDTH});
    }

    public Block(AssetManager assetManager, ColorRGBA color, Vector3f location, float[] size) {
        // Create kubus block
        this.box = new Box(size[0], size[1], size[2]);
        this.blockGeometry = new Geometry("block_" + color.toString(), box);

        this.blockMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.blockMaterial.setColor("Color", color);
        this.blockGeometry.setMaterial(blockMaterial);

        this.physics = new GhostControl(new BoxCollisionShape(new Vector3f(size[0], size[1], size[2])));
        this.blockGeometry.addControl(physics);

        this.blockGeometry.addControl(new BlockControl(color));

        this.pivot = new Node("pivotNode_block_" + color.toString());
        this.pivot.setLocalTranslation(location);
        this.pivot.attachChild(blockGeometry);

        this.blockGeometry.setLocalTranslation(0f, 0f, BLOCK_SIDE_WIDTH);
        this.color = color;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setActive(boolean active) {
        this.blockGeometry.getMaterial().getAdditionalRenderState().setWireframe(active);
    }

    public Node getPivot() {
        return this.pivot;
    }

    public Box getBox() {
        return box;
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

    public BoundingVolume getWorldBound() {
        return this.blockGeometry.getWorldBound();
    }

    public void move(float[] move) {
        pivot.move(move[0], move[1], move[2]);
    }

    public void negateMove(float[] move) {
        pivot.move(-move[0], -move[1], -move[2]);
    }

    public void rotate(float[] rotate) {
        pivot.rotate(rotate[0], rotate[1], rotate[2]);
    }

    public void negateRotate(float[] rotate) {
        pivot.rotate(-rotate[0], -rotate[1], -rotate[2]);
    }
}
