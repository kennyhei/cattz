package game.appstates;

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
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.jme3.system.AppSettings;
import game.Main;
import game.controllers.InputHandler;
import game.levels.Level;
import game.models.Block;

/*
* Cubes source code and simple classes showing how to use the framework
* can be found here:
* https://code.google.com/p/jmonkeyplatform-contributions/source/browse/#svn%2Ftrunk%2Fcubes%2FCubes%2Fsrc
*/

public class KubusScreenState extends AbstractAppState {

    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;
    private final float rotate = (float) (Math.PI/2);
    private boolean[] rotation = new boolean[]{false, false, false};

    public KubusScreenState(SimpleApplication app) {
        this.app = (Main) app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.flyCam = this.app.getFlyByCamera();
        this.settings = this.app.getContext().getSettings();
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

    // Currently controlled piece and its highlighting
    Geometry currentPiece;
    Geometry highlight;

    // Index of currently controlled piece
    int currentIndex;

    /* Block node */
    private Node terrainNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.bulletAppState = this.app.getStateManager().getState(BulletAppState.class);

        // Camera settings
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(1.6f, 2.5f, 11f)); // Default: 0f, 0f, 10f
        cam.lookAt(new Vector3f(1.6f, 2.5f, -1f), Vector3f.UNIT_Y); // Default: 0f, 0f, -1f

        flyCam.setMoveSpeed(10);

        // Add input handling
        this.inputHandler = this.app.getInputHandler();

        setUpLight();

        initWorld();
        initHighlight();
        initPuzzlePieces();

        // Custom keybindings for switching camera views
        initCameraKeys();

        // Custom keybindings for controlling puzzle pieces
        initPuzzlePieceKeys();
    }

    @Override
    public void update(float tpf) {
        // TODO
    }

    private void setUpLight() {
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        localRootNode.addLight(dl);
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
        inputManager.addMapping("BlockUp", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("BlockDown", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addMapping("BlockForward", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("BlockBackward", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("BlockRotateX", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("BlockRotateY", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("BlockRotateZ", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Change", new KeyTrigger(KeyInput.KEY_TAB));

        inputManager.addListener(actionListener, "BlockLeft");
        inputManager.addListener(actionListener, "BlockRight");
        inputManager.addListener(actionListener, "BlockUp");
        inputManager.addListener(actionListener, "BlockDown");
        inputManager.addListener(actionListener, "BlockForward");
        inputManager.addListener(actionListener, "BlockBackward");
        inputManager.addListener(actionListener, "BlockRotateX");
        inputManager.addListener(actionListener, "BlockRotateY");
        inputManager.addListener(actionListener, "BlockRotateZ");
        inputManager.addListener(actionListener, "Change");
    }

    private void initWorld() {
        Level level = this.app.getLevelManager().getLevel(0);
        this.terrainNode = level.getTerrain();

        localRootNode.setLocalScale(0.2f);
        localRootNode.attachChild(terrainNode);
    }

    private void initHighlight() {

        WireBox wbox = new WireBox(4.5f, 1.5f, 3f);
        wbox.setLineWidth(6f);
        this.highlight = new Geometry("Wirebox", wbox);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);
        this.highlight.setMaterial(mat);

        localRootNode.attachChild(this.highlight);
    }

    private void initPuzzlePieces() {

        this.puzzlePieces = new Node("Controllable Blocks");

        /*// Create two 1x1 controllable blocks
        Block block = new Block(assetManager, ColorRGBA.Blue, new Vector3f(1.5f, 7.6f, 7.6f), new float[]{1.5f, 1.5f, 1.5f});
        puzzlePieces.attachChild(block.getBlockGeometry());

        block = new Block(assetManager, ColorRGBA.Orange, new Vector3f(4.5f, 7.6f, 7.6f), new float[]{1.5f, 1.5f, 1.5f});
        puzzlePieces.attachChild(block.getBlockGeometry());
        */

        // 3x2 puzzle piece
        Block block = new Block(assetManager, ColorRGBA.randomColor(), new Vector3f(4.5f, 7.6f, 21f), new float[]{4.5f, 1.5f, 3f});
        puzzlePieces.attachChild(block.getBlockGeometry());

        // Set currently controlled piece to first puzzle piece
        currentPiece = (Geometry) puzzlePieces.getChild(0);
        highlight.setLocalTranslation(currentPiece.getLocalTranslation());

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
                currentPiece.move(0f, 3f, 0f);
            }

            if (name.equals("BlockDown") && !keyPressed) {
                currentPiece.move(0f, -3f, 0f);
            }

            if (name.equals("BlockLeft") && !keyPressed) {
                currentPiece.move(-3f, 0f, 0f);
            }

            if (name.equals("BlockRight") && !keyPressed) {
                currentPiece.move(3f, 0f, 0f);
            }

            if (name.equals("BlockForward") && !keyPressed) {
                currentPiece.move(0f, 0f, -3f);
            }

            if (name.equals("BlockBackward") && !keyPressed) {
                currentPiece.move(0f, 0f, 3f);
            }

            if (name.equals("BlockRotateX") && !keyPressed) {
                currentPiece.rotate(rotate, 0, 0);
                highlight.rotate(rotate, 0, 0);
                if (rotation[0]) {
                    currentPiece.move(0f, -1.5f, -1.5f);
                    rotation[0] = false;
                }
                else {
                    currentPiece.move(0f, 1.5f, 1.5f);
                    rotation[0] = true;
                }
            }

            if (name.equals("BlockRotateY") && !keyPressed) {
                currentPiece.rotate(0, rotate, 0);
                highlight.rotate(0, rotate, 0);
                if (rotation[1]) {
                    currentPiece.move(1.5f, 0f, 1.5f);
                    rotation[1] = false;
                }
                else {
                    currentPiece.move(-1.5f, 0f, -1.5f);
                    rotation[1] = true;
                }
            }

            if (name.equals("BlockRotateZ") && !keyPressed) {
                currentPiece.rotate(0, 0, rotate);
                highlight.rotate(0, 0, rotate);
                if (rotation[2]) {
                    currentPiece.move(3f, -3f, 0f);
                    rotation[2] = false;
                }
                else {
                    currentPiece.move(-3f, 3f, 0f);
                    rotation[2] = true;
                }
            }

            // Change controlled block
            // TODO: Refactor control change to own util class?
            if (name.equals("Change") && !keyPressed) {
                changePiece();
            }

            highlight.setLocalTranslation(currentPiece.getLocalTranslation());
                System.out.println(currentPiece.getLocalTranslation());
        }
    };

    private void changePiece() {
        ++currentIndex;

        if (currentIndex == puzzlePieces.getChildren().size()) {
            currentIndex = 0;
        }

        currentPiece = (Geometry) puzzlePieces.getChild(currentIndex);
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
