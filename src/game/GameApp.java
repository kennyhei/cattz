package game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

public class GameApp extends SimpleApplication implements PhysicsCollisionListener {

    public static void main(String[] args) {
        GameApp app = new GameApp();
        AppSettings settings = new AppSettings(true);
        settings.setAudioRenderer(null);
        settings.setFrameRate(100);
        app.showSettings = false;
        app.setSettings(settings);

        app.start();
    }

    /* Time */
    private Time time;
    private BitmapText timeText;

    /* Physics */
    private BulletAppState bulletAppState;

    /* Input */
    InputHandler inputHandler;

     /* Floor */
    private Floor floor;

    /* Player */
    private Player player;

    /* Camera */
    private ChaseCamera chaseCam;
    
    /* Kubus block */
    // Node contains blocks
    private Node blockNode;

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

        // Add input handling
        inputHandler = new InputHandler();
        inputHandler.init(inputManager);

        setUpLight();

        initCharacter();
        initChaseCamera();
        initFloor();
        initBlocks();
        initTime();
        
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

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

        // Update clock time
        timeText.setText(time.toString());

        this.camDir.set(this.cam.getDirection().multLocal(0.6f));
        this.camLeft.set(this.cam.getLeft().multLocal(0.6f));

        // Nothing was pressed, do not walk anywhere
        walkDirection.set(0f, 0f, 0f);

        if (inputHandler.LEFT) {
            walkDirection.addLocal(camLeft);
        }

        if (inputHandler.RIGHT) {
            walkDirection.addLocal(camLeft.negate());
        }

        if (inputHandler.UP) {
            walkDirection.addLocal(camDir);
        }

        if (inputHandler.DOWN) {
            walkDirection.addLocal(camDir.negate());
        }

        if (inputHandler.SPACE) {
            player.getPhysics().jump();
        }

        player.getPhysics().setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysics().getPhysicsLocation());
        
        // Rotate blocks
        for (Spatial block : blockNode.getChildren()) {
            block.rotate(0f, 2 * tpf, 0f);
        }
    }

    private void initFloor() {
        floor = new Floor(assetManager);

        // Register solid floor to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(floor.getPhysics());

        // Add floor to the scene
        rootNode.attachChild(floor.getGeometry());
    }

    private void initCharacter() {
        player = new Player(assetManager, new Vector3f(0f, 20f, -10f));

        // Register solid player to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(player.getPhysics());

        // Add player to the scene
        rootNode.attachChild(player.getModel());
    }

    private void initChaseCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, player.getModel(), inputManager);
        chaseCam.setDefaultHorizontalRotation(-1.5f);
        chaseCam.setDefaultDistance(30f);
        chaseCam.setLookAtOffset(new Vector3f(0f, 3f, 0f));
        chaseCam.setInvertVerticalAxis(true);
    }

    private void initBlocks() {
        
        blockNode = new Node("BlockNode");
        
        // Create 6 blocks
        for (int i = 0; i < 6; ++i) {
            Block kubusBlock = new Block(assetManager,
                                         ColorRGBA.randomColor(),
                                         new Vector3f(i * 10, -1f, 0f));
            
            bulletAppState.getPhysicsSpace().add(kubusBlock.getPhysics());
            blockNode.attachChild(kubusBlock.getBlockGeometry());
        }

        rootNode.attachChild(blockNode);
        
        // Rotate blocks
        for (Spatial block : blockNode.getChildren()) {
            block.rotate(.4f, .4f, 0f);
        }
    }
    
    private void initTime() {
        // Display clock with a default font
        time = new Time();

        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        timeText = new BitmapText(guiFont, false);
        timeText.setSize(guiFont.getCharSet().getRenderedSize());
        timeText.setText(time.toString());
        timeText.setLocalTranslation(10, settings.getHeight() - 10, 0);

        guiNode.attachChild(timeText);
    }

    // Remove blocks they were hit by the player
    public void collision(PhysicsCollisionEvent event) {
        Spatial a = event.getNodeA();
        Spatial b = event.getNodeB();
        
        if (a.getName().equals("Block")) {
            blockNode.detachChild(a);
            bulletAppState.getPhysicsSpace().remove(a);
        }
        
        if (b.getName().equals("Block")) {
            blockNode.detachChild(b);
            bulletAppState.getPhysicsSpace().remove(b);
        }
    }
}
