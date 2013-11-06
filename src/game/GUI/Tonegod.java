package game.GUI;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import game.Main;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

public class Tonegod {

    private Screen screen;
    private Main app;
    private Node guiNode;
    private int winCount = 0;
    private Window win;
    boolean buttonPressed;

    public Tonegod(Main app) {
        this.app = app;
        this.guiNode = app.getGuiNode();
        this.buttonPressed = false;
    }

    public final void createNewWindow(String someWindowTitle) {
        Window nWin = new Window(screen, "Window" + winCount, new Vector2f(
                (screen.getWidth() / 2) - 175, (screen.getHeight() / 2) - 100));

        nWin.setWindowTitle(someWindowTitle);
        screen.addElement(nWin);
        winCount++;
    }

    public void drawGui() {
        this.screen = new Screen(app);
        guiNode.addControl(screen);

        win = new Window(screen, "win", new Vector2f(15, 15));
        screen.addElement(win);

        ButtonAdapter clickLevel = new ButtonAdapter(screen, "Btn1", new Vector2f(15, 55)) {

            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                createNewWindow("New Window " + winCount);
                buttonPressed = true;
            }
        };

        clickLevel.setText("Level 1");
        win.addChild(clickLevel);
    }

    public void destroyGui() {
        screen.removeElement(win);
    }

    public boolean buttonPressed(){
        return buttonPressed;
    }
}
