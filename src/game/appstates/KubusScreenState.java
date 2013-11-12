package game.appstates;

import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Brick;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import game.Main;
import game.controllers.InputHandler;
import game.models.Block;

public class KubusScreenState extends AbstractAppState {

    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;

    public KubusScreenState(SimpleApplication app) {
        this.app = (Main) app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.flyCam = this.app.getFlyByCamera();
        this.settings = this.app.getContext().getSettings();
        this.bulletAppState = this.app.getStateManager().getState(BulletAppState.class);
    }

    /* Local root and gui nodes */
    private Node localRootNode = new Node("Kubus Screen RootNode");
    private Node localGuiNode = new Node("Kubus Screen GuiNode");

    /* Physics */
    private BulletAppState bulletAppState;

    /* Input */
    InputHandler inputHandler;
    
    /* Block handler */
    Node puzzlePieces;

    /* Block node */
    private Node terrainNode;

    // Rotate
    private boolean canRotate = true;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Default camera settings
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);

        flyCam.setMoveSpeed(10);

        // Add input handling
        this.inputHandler = this.app.getInputHandler();
        
        registerBlocks();
        createWorld();
        createPuzzlePieces();

        // Custom keybindings for switching camera views
        initCameraKeys();
        
        // Custom keybindings for controlling puzzle pieces
        initPuzzlePieceKeys();
    }

    @Override
    public void update(float tpf) {
        // TODO
    }

    private void initCameraKeys() {
        
        // Camera keys
        inputManager.addMapping("1st camera", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("2nd camera", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("3rd camera", new KeyTrigger(KeyInput.KEY_3));

        inputManager.addListener(actionListener, "1st camera");
        inputManager.addListener(actionListener, "2nd camera");
        inputManager.addListener(actionListener, "3rd camera");
    }
    
    private void initPuzzlePieceKeys() {
        
        // Block handling keys
        inputManager.addMapping("BlockLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("BlockRight", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("BlockUp", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("BlockDown", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("BlockForward", new KeyTrigger(KeyInput.KEY_PERIOD));
        inputManager.addMapping("BlockBackward", new KeyTrigger(KeyInput.KEY_COMMA));
        inputManager.addMapping("Change", new KeyTrigger(KeyInput.KEY_TAB));

        inputManager.addListener(actionListener, "BlockLeft");
        inputManager.addListener(actionListener, "BlockRight");
        inputManager.addListener(actionListener, "BlockUp");
        inputManager.addListener(actionListener, "BlockDown");
        inputManager.addListener(actionListener, "BlockForward");
        inputManager.addListener(actionListener, "BlockBackward");
        inputManager.addListener(actionListener, "Change");
    }

    private void registerBlocks() {

        BlockManager.register(Block_Brick.class, new BlockSkin(new BlockSkin_TextureLocation[]{
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0),
            new BlockSkin_TextureLocation(4, 0)
        }, false));
    }

    private void createWorld() {

        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this.app), new Vector3Int(1, 1, 1));
        blockTerrain.setBlockArea(new Vector3Int(0, 2, 1), new Vector3Int(6, 6, 1), Block_Brick.class);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 1), new Vector3Int(6, 1, 7), Block_Brick.class);
        
        this.terrainNode = new Node();
        terrainNode.addControl(blockTerrain);

        localRootNode.setLocalScale(0.2f);
        localRootNode.attachChild(terrainNode);
    }
    
    private void createPuzzlePieces() {
        
        this.puzzlePieces = new Node("Controllable Blocks");
        Block block = new Block(assetManager, ColorRGBA.Blue, new Vector3f(1.5f, 7.5f, 7.6f), new float[]{1.5f, 1.5f, 1.5f});
        
        puzzlePieces.attachChild(block.getBlockGeometry());
        localRootNode.attachChild(puzzlePieces);
    }
    
    private ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {

            if (name.equals("1st camera") && !keyPressed) {

                // Switch to 1st camera view
                cam.setLocation(new Vector3f(0f, 0f, 10f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }

            if (name.equals("2nd camera") && !keyPressed) {

                // Switch to 2nd camera view
                cam.setLocation(new Vector3f(0f, 0f, -10f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }

            if (name.equals("3rd camera") && !keyPressed) {

                // Switch to 3rd camera view
                cam.setLocation(new Vector3f(0f, 10f, 0f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }
            
            if (name.equals("BlockUp") && !keyPressed) {
                puzzlePieces.getChild(0).move(0f, 3f, 0f);
            }
            
            if (name.equals("BlockDown") && !keyPressed) {
                puzzlePieces.getChild(0).move(0f, -3f, 0f);
            }
            
            if (name.equals("BlockLeft") && !keyPressed) {
                puzzlePieces.getChild(0).move(-3f, 0f, 0f);
            }
            
            if (name.equals("BlockRight") && !keyPressed) {
                puzzlePieces.getChild(0).move(3f, 0f, 0f);
            }
            
            if (name.equals("BlockForward") && !keyPressed) {
                puzzlePieces.getChild(0).move(0f, 0f, -3f);
            }
            
            if (name.equals("BlockBackward") && !keyPressed) {
                puzzlePieces.getChild(0).move(0f, 0f, 3f);
            }
        }
    };

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
