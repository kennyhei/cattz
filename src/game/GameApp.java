package game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class GameApp extends SimpleApplication {

    public static void main(String[] args) {
        GameApp app = new GameApp();
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(100);
        app.setSettings(settings);
        
        app.start();
    }
    
    /* Physics */
    private BulletAppState bulletAppState;
    
    /* Geometries and physical bodies */
    private Box floorBox;
    private RigidBodyControl floorPhysics;
    
    @Override
    public void simpleInitApp() {
        // Set up physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Initialize floor
        initFloor();
    }

    private void initFloor() {
        // Create floor
        floorBox = new Box(15f, 0.1f, 10f);
        floorBox.scaleTextureCoordinates(new Vector2f(3, 6));
        Geometry floor = new Geometry("Floor", floorBox);
        floor.setLocalTranslation(0, -4f, 0);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Set up texture to floor material
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        mat.setTexture("ColorMap", tex3);
        
        floor.setMaterial(mat);
        
        // Make the floor physical
        floorPhysics = new RigidBodyControl(0f);
        floor.addControl(floorPhysics);
        
        // Register solid floor to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(floorPhysics);
        
        // Add floor to the scene
        rootNode.attachChild(floor);
    }
    
}
