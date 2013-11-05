package game.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import game.GUI.Tonegod;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;



public class StartScreenState extends AbstractAppState {

    private SimpleApplication app;
    private ViewPort viewPort;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private Node localRootNode = new Node("Start Screen RootNode");
    private Node localGuiNode = new Node("Start Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Black;
    private Tonegod tonegod;

    public StartScreenState(SimpleApplication app) {
        this.app = app;
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.assetManager = app.getAssetManager();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        viewPort.setBackgroundColor(backgroundColor);
        
        // tonegod gui start
        tonegod = new Tonegod(this.app);
        tonegod.drawGui();
        
        // Menu message
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText displaytext = new BitmapText(guiFont);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.setText("Start screen. Press BACKSPACE to start the game");

        displaytext.setLocalTranslation(this.app.getContext().getSettings().getWidth() / 2 - displaytext.getLineWidth() / 2,
                                        this.app.getContext().getSettings().getHeight() / 2 + displaytext.getLineHeight(), 0);

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
        tonegod.destroyGui();
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);
    }
}
