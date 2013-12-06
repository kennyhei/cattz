package game.appstates;

import com.cubes.CubesSettings;
import com.cubes.test.CubesTestAssets;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import game.Main;
import game.levels.Level;
import game.models.Block;
import java.util.List;

/*
 * Cubes source code and simple classes showing how to use the framework
 * can be found here:
 * https://code.google.com/p/jmonkeyplatform-contributions/source/browse/#svn%2Ftrunk%2Fcubes%2FCubes%2Fsrc
 */
public class KubusScreenState extends AbstractAppState {
    
    private final float BLOCK_WIDTH = 3f;
    private Main app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;
    private final float rotateAmount = (float) (Math.PI / 2);
    
    public KubusScreenState() {
        this.app = Main.getApp();
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

    /* Block handler */
    List<Block> puzzlePieces;
    // Currently controlled piece and its highlighting
    Block currentPiece;
//    Geometry highlight;
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
        
        
        setUpLight();
        
        initWorld();
        initPuzzlePieces();
        updateHighlight();

        // Custom keybindings for switching camera views
        initCameraKeys();

        // Custom keybindings for controlling puzzle pieces
        initPuzzlePieceKeys();

//        System.out.println(CubesTestAssets.getSettings(app).getBlockMaterial());
    }
    
    @Override
    public void update(float tpf) {
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
        Level level = this.app.getLevelManager().getCurrentLevel();
        this.terrainNode = level.getTerrain();
        
        localRootNode.setLocalScale(0.2f);
        localRootNode.attachChild(terrainNode);
    }
    
    private void updateHighlight() {
//        if (highlight != null) {
//            localRootNode.detachChild(highlight);
//        }
//
//        WireBox wbox = new WireBox(currentPiece.getBox().xExtent, currentPiece.getBox().yExtent, currentPiece.getBox().zExtent); // .getBox().xExtent, currentPiece.getBox().yExtent, currentPiece.getBox().zExtent);
//        wbox.setLineWidth(6f);
//
//        this.highlight = new Geometry("Wirebox", wbox);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.getAdditionalRenderState().setWireframe(true);
//        mat.setColor("Color", ColorRGBA.Green);
//        this.highlight.setMaterial(mat);
//        localRootNode.attachChild(this.highlight);
//
//        highlight.setLocalTranslation(currentPiece.getBlockGeometry().getLocalTranslation());
//        highlight.setLocalRotation(currentPiece.getBlockGeometry().getLocalRotation());
    }
    
    private void initPuzzlePieces() {
        this.puzzlePieces = this.app.getLevelManager().getCurrentLevel().getSolution(); // .getPuzzlePieces();

        currentPiece = puzzlePieces.get(0);
        currentPiece.setActive(true);
        
        for (Block block : puzzlePieces) {
            
            if (block.getPivot() != null) {
                System.out.println("attaching pivot to kubus screen");
                localRootNode.attachChild(block.getPivot());
            } else {
                localRootNode.attachChild(block.getBlockGeometry());
                
            }
        }
    }
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            handleCamera(name, keyPressed);
            handleMovement(name, keyPressed);

            // Change controlled block
            // TODO: Refactor control change to own util class?
            if (name.equals("Change") && !keyPressed) {
                changePiece();
            }
            
            updateHighlight();
//            highlight.setLocalTranslation(currentPiece.getLocalTranslation());

            if (app.getLevelManager().getCurrentLevel().checkBlocks()) {
                System.out.println("Congrats, you've cleared this level!");
            }
            
        }
        
        private void handleCamera(String name, boolean keyPressed) {
            if (keyPressed) {
                return;
            }
            
            if (name.equals("1st camera")) {

                // Switch to 1st camera view
                cam.setLocation(new Vector3f(0f, 0f, 10f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }
            
            if (name.equals("2nd camera")) {

                // Switch to 2nd camera view
                cam.setLocation(new Vector3f(0f, 0f, -10f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }
            
            if (name.equals("3rd camera")) {

                // Switch to 3rd camera view
                cam.setLocation(new Vector3f(0f, 10f, 0f));
                cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
            }
        }
        
        private void handleMovement(String eventName, boolean keyPressed) {
            movePiece(eventName, keyPressed);
            rotatePiece(eventName, keyPressed);
            System.out.println("");
            System.out.println("");
            
        }
        
        private void movePiece(String eventName, boolean keyPressed) {
            if (keyPressed) {
                return;
            }
            
            float[] move = {0f, 0f, 0f};
            
            if (eventName.equals("BlockLeft")) {
                move[0] = -BLOCK_WIDTH;
            }
            
            if (eventName.equals("BlockRight")) {
                move[0] = BLOCK_WIDTH;
            }
            
            if (eventName.equals("BlockUp")) {
                move[1] = BLOCK_WIDTH;
            }
            
            if (eventName.equals("BlockDown")) {
                move[1] = -BLOCK_WIDTH;
            }
            
            if (eventName.equals("BlockForward")) {
                move[2] = -BLOCK_WIDTH;
            }
            
            if (eventName.equals("BlockBackward")) {
                move[2] = BLOCK_WIDTH;
            }
            
            currentPiece.move(move);
            
            if (!currentPieceInBounds()) {
                currentPiece.negateMove(move);
            }
        }
        
        private void rotatePiece(String eventName, boolean keyPressed) {
            if (!keyPressed) {
                return;
            }
            
            float[] rotate = {0f, 0f, 0f};
            
            if (eventName.equals("BlockRotateX")) {
                rotate[0] = rotateAmount;
            }
            
            if (eventName.equals("BlockRotateY")) {
                rotate[1] = rotateAmount;
            }
            
            if (eventName.equals("BlockRotateZ")) {
                rotate[2] = rotateAmount;
            }
            
            currentPiece.rotate(rotate);
            
            if (!currentPieceInBounds()) {
                currentPiece.negateRotate(rotate);
            }
        }
        
        private boolean currentPieceInBounds() {
            BoundingBox terrainBox = (BoundingBox) terrainNode.getWorldBound();
            BoundingBox pieceBox = (BoundingBox) currentPiece.getWorldBound();
            
            
            for (Block block : puzzlePieces) {
                if (block == currentPiece) {
                    continue;
                }
                
                System.out.println("todo:? if collides with " + block);
            }
            
            CollisionResults results = new CollisionResults();
            terrainNode.collideWith(currentPiece.getWorldBound(), results);
            if (results.size() > 0) {
                System.out.println("current piece collides with the terrain");
//                return false;
            }

            // magic number due to the piece being a bit bigger than the boxes
            if (terrainBox.clone().mergeLocal(pieceBox).getVolume() > terrainBox.getVolume()) {
                System.out.println("current piece would be outside the box");
//                return false;
            }
            
            return true;
        }
    };
    
    private void changePiece() {
        currentPiece.setActive(false);
        currentIndex = (currentIndex + 1) % puzzlePieces.size();
        currentPiece = puzzlePieces.get(currentIndex);
        
        currentPiece.setActive(true);
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
