package game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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
    
    /* Input */
    InputHandler inputHandler;
    
    /* Geometries and physical bodies */
    private Box floorBox;
    private RigidBodyControl floorPhysics;
    
    /* Player */
    private CharacterControl player;
    private Node playerModel;
    
    /* Camera */
    private ChaseCamera chaseCam;
    
    // Temporary vectors used on each frame.
    // They are here to avoid instantiating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Vector3f walkDirection = new Vector3f();
    
    @Override
    public void simpleInitApp() {
        // Set up physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        
        // Add input handling
        inputHandler = new InputHandler();
        inputHandler.init(inputManager);
        
        setUpLight();
        
        initCharacter();
        initChaseCamera();
        initFloor();
    }
    
    // Light makes models visible
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        this.camDir.set(this.cam.getDirection().multLocal(0.6f));
        this.camLeft.set(this.cam.getLeft().multLocal(0.6f));
        walkDirection.set(0f, 0f, 0f);
        
        if (inputHandler.LEFT) {
            walkDirection.set(camLeft);
        }
        
        if (inputHandler.RIGHT) {
            walkDirection.set(camLeft.negate());
        }
        
        if (inputHandler.UP) {
            walkDirection.set(camDir);
        }
        
        if (inputHandler.DOWN) {
            walkDirection.set(camDir.negate());
        }
        
        if (inputHandler.SPACE) {
            player.jump();
        }
        
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
    
    private void initFloor() {
        // Create floor
        floorBox = new Box(300f, 0.1f, 200f);
        floorBox.scaleTextureCoordinates(new Vector2f(15, 30));
        Geometry floor = new Geometry("Floor", floorBox);
        floor.setLocalTranslation(0, -6f, 0);
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

    private void initCharacter() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(3f, 4f);
        player = new CharacterControl(capsule, 0.01f);
        playerModel = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        playerModel.addControl(player);
        player.setPhysicsLocation(new Vector3f(0f, 0f, -10f));
        
        // Register solid player to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(player);
        
        rootNode.attachChild(playerModel);
    }

    private void initChaseCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, playerModel, inputManager);
        chaseCam.setDefaultDistance(30f);
        chaseCam.setInvertVerticalAxis(true);
    }
}
