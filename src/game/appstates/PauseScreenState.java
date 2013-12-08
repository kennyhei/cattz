package game.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import game.Main;

public class PauseScreenState extends AbstractAppState {

    private SimpleApplication app;
    private ViewPort viewPort;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private Node localRootNode = new Node("Pause Screen RootNode");
    private Node localGuiNode = new Node("Pause Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Pink;

    public PauseScreenState() {
        this.app = Main.getApp();
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);

        // Menu message
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText displaytext = new BitmapText(guiFont);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.setText("               Game paused \n"
                + "\n"
                + "Game Controls:                   Kubus Controls:\n"
                + "SPACE - jump                 TAB - switch blocks \n "
                + "W - forward                      G - Rotate block by x axis \n "
                + "A - left                         H - Rotate block by y axis \n "
                + "D - right                        J - Rotate block by z axis \n "
                + "S - down                         PageUp - Move block up \n "
                + "                                     PageDown - Move block down \n "
                + "                                     Left arrow - Move block left \n "
                + "                                     Right arrow - Move block right \n "
                + "Global Controls:             Up arrow - Move block forward \n "
                + "P - pause                    Down arrow - Move block backward \n"
                + "ESC - exit game              W,A,S,D - Move camera");

        displaytext.setLocalTranslation(this.app.getContext().getSettings().getWidth() / 2 - displaytext.getLineWidth() / 2,
                this.app.getContext().getSettings().getHeight() / 2 + 3*displaytext.getLineHeight(), 0);

        localGuiNode.attachChild(displaytext);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        viewPort.setBackgroundColor(backgroundColor);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
}
