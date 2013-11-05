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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import game.controllers.InputHandler;

public class KubusScreenState extends AbstractAppState {

    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppSettings settings;
    private InputManager inputManager;
    private Camera cam;
    private FlyByCamera flyCam;

    public KubusScreenState(SimpleApplication app) {
        this.app = app;
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

    /* Random box */
    private Box box;
    private Geometry boxGeom;
    
    /* Directions to show the Kubus puzzle */
        
    // From above
    private Vector3f above;
    
    // From left
    private Vector3f left;
    
    // Rotate
    private boolean canRotate = true;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Directions setup
        above = new Vector3f(0f, 0f, 0f);

        // Default camera settings
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);

        // Add input handling
        inputHandler = new InputHandler();
        inputHandler.init(inputManager);
        
        this.box = new Box(1f, 1f, 1f);
        this.boxGeom = new Geometry("Box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        boxGeom.setMaterial(mat);
        boxGeom.setLocalTranslation(0f, 1f, 1f);
        
        Box second = new Box(1f, 2f, 1f);
        Geometry secondBox = new Geometry("Box", second);
        Material secondMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        secondBox.setMaterial(secondMat);
        secondBox.setLocalTranslation(-1f, 0f, 0f);

        localRootNode.attachChild(boxGeom);
        localRootNode.attachChild(secondBox);
        
        // Custom keybindings for switching camera views
        initCameraKeys();
    }

    @Override
    public void update(float tpf) {

        if (canRotate) {
             boxGeom.rotate(0f, 2 * tpf, 0f);
        }

        if (inputHandler.LEFT) {
            canRotate = false;
        }

        if (inputHandler.RIGHT) {
            canRotate = true;
        }
    }
    
    private void initCameraKeys() {
        inputManager.addMapping("1st camera", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("2nd camera", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("3rd camera", new KeyTrigger(KeyInput.KEY_3));
        
        inputManager.addListener(actionListener, "1st camera");
        inputManager.addListener(actionListener, "2nd camera");
        inputManager.addListener(actionListener, "3rd camera");
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
