/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import game.appstates.GameScreenState;
import game.appstates.StartScreenState;

public class Main extends SimpleApplication {

    /* States */
    private GameScreenState gameRunningState;
    private StartScreenState startScreenState;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setAudioRenderer(null);
        settings.setFrameRate(100);
        app.showSettings = false;
        app.setSettings(settings);
        app.start();
    }

    // Initialize the game here
    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        gameRunningState = new GameScreenState(this);
        startScreenState = new StartScreenState(this);

        stateManager.attach(startScreenState);

        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_BACK));
        inputManager.addListener(actionListener, "Start");
    }

    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Start") && !isPressed) {
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                System.out.println("Starting game...");
            }
        }
    };
}