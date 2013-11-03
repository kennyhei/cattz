package game.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

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

    /* Random box */
    private Box box;
    private Geometry boxGeom;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Back to default camera settings
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);

        this.box = new Box(1f, 1f, 1f);
        this.boxGeom = new Geometry("Box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        boxGeom.setMaterial(mat);
        boxGeom.setLocalTranslation(0f, 1f, 1f);

        localRootNode.attachChild(boxGeom);
    }

    @Override
    public void update(float tpf) {
        boxGeom.rotate(0f, 2 * tpf, 0f);
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
