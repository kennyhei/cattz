package game.appstates;

import com.cubes.BlockChunkControl;
import com.cubes.BlockChunkListener;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Grass;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import game.Main;
import game.models.Block;
import game.controllers.BlockControl;
import game.models.HudBlock;
import game.controllers.InputHandler;
import game.models.Player;
import game.models.Time;
import java.util.ArrayList;
import java.util.Random;

public class GameScreenState extends AbstractAppState implements PhysicsCollisionListener {

    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;
    private ViewPort viewPort;

    public GameScreenState(SimpleApplication app) {
        this.app = (Main) app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.flyCam = this.app.getFlyByCamera();
        this.settings = this.app.getContext().getSettings();
        this.viewPort = this.app.getViewPort();
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

    /* Terrain */
    private Node terrainNode;

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
        this.inputHandler = this.app.getInputHandler();

        setUpLight();

        initCharacter();
        initChaseCamera();
        initWorld();
        initBlocks();
        initTime();
        initText();

        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void setUpLight() {
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        localRootNode.addLight(dl);

        /* Drop shadows */
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(dl);
        dlsr.setShadowIntensity(0.7f);
        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(dl);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
    }

    @Override
    public void update(float tpf) {

        // Blocks have been collected
        if (blockNode.getChildren().isEmpty()) {
            localGuiNode.attachChild(text);

            // Switching to kubus world ok
            this.app.setSwitch(true);
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

        if (player.getPhysics().getPhysicsLocation().y < -40) {

            // Remove solid player from PhysicsSpace
            bulletAppState.getPhysicsSpace().remove(player.getPhysics());

            // Detach player from the scene
            localRootNode.detachChild(player.getModel());

            initCharacter();
            initChaseCamera();
            this.time.add(5);
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

    private void initCharacter() {
        player = new Player(assetManager, new Vector3f(30f, 15f, 10f));
        

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

    private void initWorld() {

        // Register blocks
        CubesTestAssets.registerBlocks();

        CubesTestAssets.initializeEnvironment(this.app);
        CubesTestAssets.initializeWater(this.app);

        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this.app), new Vector3Int(14, 1, 14));
        blockTerrain.setBlocksFromHeightmap(new Vector3Int(0, -5, -5), "Textures/maze-h2.jpg", 17, Block_Grass.class);

        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // Make blocks solid
        blockTerrain.addChunkListener(new BlockChunkListener() {

            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk) {

                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                RigidBodyControl rigidBodyControl = optimizedGeometry.getControl(RigidBodyControl.class);

                if (rigidBodyControl == null) {
                    rigidBodyControl = new RigidBodyControl(0);
                    optimizedGeometry.addControl(rigidBodyControl);
                    bulletAppState.getPhysicsSpace().add(rigidBodyControl);
                }

                rigidBodyControl.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });

        localRootNode.attachChild(terrainNode);
    }

    private void initBlocks() {

        ArrayList<Integer> usedLocations = new ArrayList<Integer>();
        float[][] locations = Block.locations;
        Random random = new Random();

        blockNode = new Node("BlockNode");

        // Create 6 blocks and hud blocks
        for (int i = 0; i < 6; ++i) {
            Block kubusBlock = new Block(assetManager,
                                         ColorRGBA.randomColor(),
                                         new Vector3f(i * 10, 20f, -5f), new float[]{2f, 4f, 1f});

            // Set random location to kubus block
            while (true) {
                int index = random.nextInt(locations.length);

                if (!usedLocations.contains(index)) {
                    float[] newLocation = locations[index];
                    usedLocations.add(index);
                    kubusBlock.setLocation(new Vector3f(newLocation[0], newLocation[1], newLocation[2]));
                    break;
                }
            }

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

    public Time getTime() {
        return this.time;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        chaseCam.setInvertVerticalAxis(false);
        rootNode.detachChildNamed("Sky");
        viewPort.clearProcessors();
        bulletAppState.getPhysicsSpace().removeCollisionListener(this);

        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
}
