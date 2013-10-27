package game;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class Floor {
    
    private Box floorBox;
    private Geometry floor;
    private RigidBodyControl floorPhysics;
    
    public Floor(AssetManager assetManager) {
        
        // Create floor
        floorBox = new Box(300f, 0.1f, 200f);
        floorBox.scaleTextureCoordinates(new Vector2f(15, 30));
        floor = new Geometry("Floor", floorBox);
        floor.setLocalTranslation(0, -6f, 0);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Set up texture to floor material
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex3);
        
        floor.setMaterial(mat);
        
        // Make the floor physical
        floorPhysics = new RigidBodyControl(0f);
        floor.addControl(floorPhysics);
    }
    
    public RigidBodyControl getPhysics() {
        return floorPhysics;
    }
    
    public Geometry getGeometry() {
        return floor;
    }
}
