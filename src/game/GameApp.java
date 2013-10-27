package game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

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

     /* Floor */
    private Floor floor;

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
            player.jump();
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    private void initFloor() {
        floor = new Floor(assetManager);

        // Register solid floor to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(floor.getPhysics());

        // Add floor to the scene
        rootNode.attachChild(floor.getGeometry());
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
