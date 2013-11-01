package game.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import game.models.Block;
import game.controllers.BlockControl;
import game.models.Floor;
import game.models.HudBlock;
import game.controllers.InputHandler;
import game.models.Player;
import game.models.Time;

public class GameScreenState extends AbstractAppState implements PhysicsCollisionListener {

    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;

    public GameScreenState(SimpleApplication app) {
        this.app = app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.flyCam = this.app.getFlyByCamera();
        this.settings = this.app.getContext().getSettings();
    }

    /* Local root and gui nodes */
    private Node localRootNode = new Node("Game Screen RootNode");
    private Node localGuiNode = new Node("Game Screen GuiNode");

    /* Physics */
    private BulletAppState bulletAppState;

    /* Time */
    private Time time;
    private BitmapText timeText;

    /* Text */
    private BitmapText text;

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
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

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
        initText();

        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        localRootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        localRootNode.addLight(dl);
    }

    @Override
    public void update(float tpf) {

        // Blocks have been collected
        if (blockNode.getChildren().isEmpty()) {
            localGuiNode.attachChild(text);
        }

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

    // Remove blocks if they were hit by the player
    public void collision(PhysicsCollisionEvent event) {

        Spatial a = event.getNodeA();
        Spatial b = event.getNodeB();

        if (a.getName().equals("Block")) {
            a.getControl(BlockControl.class).getHudBlock().colour();
            blockNode.detachChild(a);
            bulletAppState.getPhysicsSpace().remove(a);
        }

        if (b.getName().equals("Block")) {
            b.getControl(BlockControl.class).getHudBlock().colour();
            blockNode.detachChild(b);
            bulletAppState.getPhysicsSpace().remove(b);
        }
    }

    private void initFloor() {
        floor = new Floor(assetManager);

        // Register solid floor to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(floor.getPhysics());

        // Add floor to the scene
        localRootNode.attachChild(floor.getGeometry());
    }

    private void initCharacter() {
        player = new Player(assetManager, new Vector3f(0f, 20f, -10f));

        // Register solid player to PhysicsSpace
        bulletAppState.getPhysicsSpace().add(player.getPhysics());

        // Add player to the scene
        localRootNode.attachChild(player.getModel());
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

        // Create 6 blocks and hud blocks
        for (int i = 0; i < 6; ++i) {
            Block kubusBlock = new Block(assetManager,
                                         ColorRGBA.randomColor(),
                                         new Vector3f(i * 10, -1f, 0f));

            BlockControl c = kubusBlock.getBlockGeometry().getControl(BlockControl.class);

            HudBlock hudBlock = new HudBlock(assetManager,
                                             c.getColor(),
                                             new Vector3f(80 + i * 30, settings.getHeight() - 25, 0));

            c.setHudBlock(hudBlock);
            localGuiNode.attachChild(hudBlock.getGeometry());

            bulletAppState.getPhysicsSpace().add(kubusBlock.getPhysics());
            blockNode.attachChild(kubusBlock.getBlockGeometry());
        }

        localRootNode.attachChild(blockNode);

        // Rotate blocks
        for (Spatial block : blockNode.getChildren()) {
            block.rotate(.4f, .4f, 0f);
        }
    }

    private void initTime() {
        // Display time
        time = new Time();

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        timeText = new BitmapText(guiFont, false);
        timeText.setSize(guiFont.getCharSet().getRenderedSize());
        timeText.setText(time.toString());
        timeText.setLocalTranslation(10, settings.getHeight() - 10, 0);

        localGuiNode.attachChild(timeText);
    }

    private void initText() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(25.5f);
        text.setText("Press Enter to continue.");
        text.setLocalTranslation(settings.getWidth() / 2 - text.getLineWidth() / 2,
                                 settings.getHeight() / 2 + text.getLineHeight(), 0);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
}
