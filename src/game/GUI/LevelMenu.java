package game.GUI;

import com.jme3.font.BitmapFont.Align;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import game.Main;
import game.managers.LevelManager;
import java.util.SortedMap;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

public class LevelMenu {

    private Screen screen;
    private Node guiNode;
    private int winCount = 0;
    private Window win;
    boolean buttonPressed;

    public LevelMenu() {
        this.guiNode = Main.getApp().getGuiNode();
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
        this.screen = new Screen(Main.getApp());
        guiNode.addControl(screen);

        win = new Window(screen, "win", new Vector2f(15, 15));
        win.setText("Worlds");
        win.setTextAlign(Align.Center);
        win.setTextPadding(8f);

        screen.addElement(win);

        Label info = new Label(screen, new Vector2f(15, 30), new Vector2f(320, 30));
        info.setText("You are entering a world, where\nyou need to collect colorful blocks!");
        info.setTextAlign(Align.Center);
        win.addChild(info);

        final LevelManager levelManager = Main.getApp().getLevelManager();

        SortedMap<Integer, String> levels = levelManager.getLevelOrdering();

        int buttonNum = 0;

        for (final Integer levelIndex : levels.keySet()) {
            System.out.println("Creating button for level index: " + levelIndex + " (" + levels.get(levelIndex) + ")");

            Vector2f buttonPosition = new Vector2f(15, 80 + buttonNum * 40);
            Vector2f buttonSize = new Vector2f(320, 30);

            ButtonAdapter levelButton = new ButtonAdapter(screen, "Btn" + levelIndex, buttonPosition, buttonSize) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    if (!levelManager.isEnabled(levelIndex)) {
                        return;
                    }

                    //createNewWindow("New Window " + winCount);
                    buttonPressed = true;
                    ((Main) app).setActiveLevel(levelIndex);
                }
            };

            if (!levelManager.isEnabled(levelIndex)) {
                levelButton.setGlobalAlpha(0.2f);
                levelButton.setIgnoreMouse(true);
                levelButton.setFontColor(ColorRGBA.Gray);
            }

            levelButton.setText(levels.get(levelIndex));

            win.addChild(levelButton);

            buttonNum++;
        }
    }

    public void destroyGui() {
        screen.removeElement(win);
    }

    public boolean buttonPressed() {
        return buttonPressed;
    }
}
