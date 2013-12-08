package game.appstates;

import com.cubes.BlockChunkControl;
import com.cubes.BlockChunkListener;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Grass;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import game.Main;
import game.models.Block;
import game.controllers.BlockControl;
import game.levels.Level;
import game.models.HudBlock;
import game.models.Player;
import game.models.Time;
import java.util.ArrayList;
import java.util.List;
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

    public GameScreenState() {
        this.app = (Main) Main.getApp();
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

    /* Terrain */
    private Node terrainNode;

    /* Player */
    private Player player;

    /* Camera */
    private CameraNode cameraNode;

    /* Kubus block */
    // Node contains blocks
    private Node blockNode;
    private boolean finished = false;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        setUpLight();

        initCharacter();
        initCamera();
        initWorld();
        initBlocks();
        initTime();
        initText();

        initInput();

        // Don't trigger built-in listeners (Space)
        inputManager.clearRawInputListeners();

        bulletAppState.getPhysicsSpace().addCollisionListener(this);

        localRootNode.attachChild(player.getPlayerNode());
    }

    private void setUpLight() {
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        localRootNode.addLight(dl);

        /* Drop shadows */
        final int SHADOWMAP_SIZE = 1024;
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
        bulletAppState.update(tpf);

        // Blocks have been collected
        if (blockNode.getChildren().isEmpty()) {
            localGuiNode.attachChild(text);

            // Switching to kubus world ok
            this.app.setNextState(KubusScreenState.class);
            finished = true;
        }

        if (!finished) {
            // Update clock time
            timeText.setText(time.toString());
        }

        player.update(tpf);

        for (Spatial block : blockNode.getChildren()) {
            block.rotate(0f, .02f, 0f);
        }
    }

    // Remove blocks if they were hit by the player
    public void collision(PhysicsCollisionEvent event) {
        Spatial a = event.getNodeA();
        Spatial b = event.getNodeB();

        if (a.getName().startsWith("block_")) {
            a.getControl(BlockControl.class).getHudBlock().colour();
            blockNode.detachChild(a);

            localRootNode.detachChildNamed("bling_" + a.getName());
            bulletAppState.getPhysicsSpace().remove(a);
        }

        if (b.getName().startsWith("block_")) {
            b.getControl(BlockControl.class).getHudBlock().colour();
            blockNode.detachChild(b);

            localRootNode.detachChildNamed("bling_" + b.getName());
            bulletAppState.getPhysicsSpace().remove(b);
        }
    }

    private void initCharacter() {
        player = new Player(assetManager, new Vector3f(30f, 15f, 10f));
        bulletAppState.getPhysicsSpace().add(player.getPhysicsCharacter());
    }

    private void initCamera() {
        flyCam.setEnabled(false);

        cameraNode = new CameraNode("camera", cam);
        cameraNode.setControlDir(ControlDirection.SpatialToCamera);
        cameraNode.setLocalTranslation(new Vector3f(0, 8, -8));
        Vector3f lookAt = player.getModel().getLocalTranslation().clone();
        lookAt.y = 7f;
        cameraNode.lookAt(lookAt, Vector3f.UNIT_Y);
        player.getPlayerNode().attachChild(cameraNode);
    }

    private void initWorld() {

        Level current = Main.getApp().getLevelManager().getCurrentLevel();

        // Register blocks
        CubesTestAssets.registerBlocks();

//        CubesTestAssets.getSettings(this.app).getBlockMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.FrontAndBack);
//
//        CubesTestAssets.getSettings(this.app).getBlockMaterial().getAdditionalRenderState().setDepthTest(true); // .setFaceCullMode(RenderState.FaceCullMode.FrontAndBack);


        // disabled due to laptop hw issues..
//        CubesTestAssets.initializeEnvironment(this.app);
//        CubesTestAssets.initializeWater(this.app);

        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this.app), new Vector3Int(14, 1, 14));
        blockTerrain.setBlocksFromHeightmap(new Vector3Int(0, 1, -5), current.getLevelHeightMap(), 3, Block_Grass.class);
//        Material blockMaterial = blockTerrain.getSettings().getBlockMaterial();
//        blockMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
//        blockMaterial.getAdditionalRenderState().setDepthTest(true); // .setFaceCullMode(RenderState.FaceCullMode.FrontAndBack);

        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        System.out.println("Updating spatial");
//        blockTerrain.updateSpatial();
        System.out.println("done..");

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
        Level currentLevel = Main.getApp().getLevelManager().getCurrentLevel();

        ArrayList<Integer> usedLocations = new ArrayList<Integer>();
        float[][] locations = Block.locations;
        Random random = new Random();

        blockNode = new Node("BlockNode");

        // Create blocks for the level blocks
        List<Block> levelBlocks = currentLevel.getBlocks();
        int idx = 0;
        for (Block tmpBlock : levelBlocks) {
            // Do not change tmpBlock as they are used in the kubus game

            Block kubusBlock = new Block(assetManager,
                    tmpBlock.getColor(),
                    new Vector3f(idx * 10, 20f, -5f), new float[]{2f, 4f, 1f});

            while (true) {
                int index = random.nextInt(locations.length);

                float[] loc = locations[index];

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
                    new Vector3f(80 + idx * 30, settings.getHeight() - 25, 0));


            c.setHudBlock(hudBlock);
            localGuiNode.attachChild(hudBlock.getGeometry());

            bulletAppState.getPhysicsSpace().add(kubusBlock.getPhysics());

            Spatial blockSpatial = kubusBlock.getBlockGeometry();
            blockNode.attachChild(blockSpatial);

            // rotate blocks
            blockSpatial.rotate(.4f, .4f, 0f);

            // add bling
            Node blingNode = new Node("bling_" + blockSpatial.getName());
            blingNode.setLocalTranslation(blockSpatial.getLocalTranslation());
            blingNode.move(0f, -4f, 0f);

            ParticleEmitter sparks =
                    new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            Material particleMaterial = new Material(assetManager,
                    "Common/MatDefs/Misc/Particle.j3md");
            particleMaterial.setTexture("Texture", assetManager.loadTexture(
                    "Effects/Explosion/roundspark.png"));
            sparks.setMaterial(particleMaterial);
            sparks.setImagesX(4);
            sparks.setImagesY(4);

            sparks.setEndColor(c.getColor());
            sparks.setStartColor(c.getColor());

            sparks.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
            sparks.setStartSize(1.5f);
            sparks.setEndSize(0.5f);
            sparks.setGravity(0, -0.5f, 0);
            sparks.setLowLife(2f);
            sparks.setHighLife(8f);
            sparks.getParticleInfluencer().setVelocityVariation(0.8f);

            blingNode.attachChild(sparks);
            localRootNode.attachChild(blingNode);

            idx++;
        }

        localRootNode.attachChild(blockNode);
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
//        chaseCam.setInvertVerticalAxis(false);
        rootNode.detachChildNamed("Sky");
        viewPort.clearProcessors();
        inputManager.removeListener(player);
        bulletAppState.getPhysicsSpace().removeCollisionListener(this);

        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }

    private void initInput() {
        inputManager.addMapping(Player.LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(Player.RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(Player.FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(Player.BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(Player.JUMP, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(player, Player.LEFT);
        inputManager.addListener(player, Player.RIGHT);
        inputManager.addListener(player, Player.FORWARD);
        inputManager.addListener(player, Player.BACKWARD);
        inputManager.addListener(player, Player.JUMP);
    }
}
