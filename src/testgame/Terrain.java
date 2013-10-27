package testgame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

public class Terrain {

    private TerrainQuad terrain;
    private Geometry boundingBoxGeometry;
    public static final String NAME = "terrain";

    public Terrain(AssetManager assetManager) {
        Texture heightmapTexture = assetManager.loadTexture("Textures/heightmap.png");
        ImageBasedHeightMap heightmap = new ImageBasedHeightMap(heightmapTexture.getImage());
        heightmap.setHeightScale(24f);
        heightmap.load();

        Material terrainMaterial = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        terrainMaterial.setTexture("ColorMap", heightmapTexture);

        terrain = new TerrainQuad(Terrain.NAME, 65, 513, heightmap.getHeightMap());

        terrain.setMaterial(terrainMaterial);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f);

        terrain.addControl(new RigidBodyControl(0));


        BoundingVolume bounds = terrain.getWorldBound();

        WireBox boundingBox = new WireBox();
        boundingBox.fromBoundingBox((BoundingBox) bounds);
        boundingBoxGeometry = new Geometry("boundingbox", boundingBox);
        Material boundingBoxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boundingBoxGeometry.setMaterial(boundingBoxMaterial);
        boundingBoxGeometry.updateModelBound();
        boundingBoxGeometry.setLocalTranslation(0, -100, 0);
        boundingBoxGeometry.scale(1f, 3f, 1f);

    }

    public void attachAndAddLodControl(Node nodeToAttachTo, PhysicsSpace physicsSpace, Camera camera) {
        nodeToAttachTo.attachChild(terrain);
        physicsSpace.add(terrain);

        nodeToAttachTo.attachChild(boundingBoxGeometry);

        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        terrain.addControl(control);
    }
}
