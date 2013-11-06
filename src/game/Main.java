package game;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.system.AppSettings;
import game.GUI.Tonegod;
import game.appstates.GameScreenState;
import game.appstates.KubusScreenState;
import game.appstates.PauseScreenState;
import game.appstates.StartScreenState;

public class Main extends SimpleApplication {

    /* States */
    private GameScreenState gameRunningState;
    private StartScreenState startScreenState;
    private KubusScreenState kubusScreenState;
    private PauseScreenState pauseScreenState;
    private Tonegod tonegod;

    private boolean switchToKubus = false;

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

        // creating the GUI
        tonegod = new Tonegod(this);

        gameRunningState = new GameScreenState(this);
        startScreenState = new StartScreenState(this, tonegod);
        kubusScreenState = new KubusScreenState(this);
        pauseScreenState = new PauseScreenState(this);

        stateManager.attach(startScreenState);

        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_BACK));
        inputManager.addMapping("Continue", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addListener(actionListener, "Start");
        inputManager.addListener(actionListener, "Continue");
        inputManager.addListener(actionListener, "Pause");

        // Tonegodgui click listener
        inputManager.addMapping("click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "click");
    }

    public void setSwitch(boolean switchOk) {
        this.switchToKubus = switchOk;
    }

    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {

// this is a tonegod-related listener that does not work as it is
//            if (name.equals("click") && !isPressed) {
//               // we know something has been clicked
//                System.out.println("KLIKKKK");
//               if (tonegod.buttonPressed()) {
//                   stateManager.detach(startScreenState);
//                   stateManager.attach(gameRunningState);
//                   gameRunningState.setEnabled(true);
//                   System.out.println("level 1");
//               }
//            }

            if (name.equals("Start") && !isPressed) {
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                gameRunningState.setEnabled(true);
                System.out.println("Starting game...");
            }

            if (name.equals("Continue") && !isPressed && switchToKubus) {
                stateManager.detach(gameRunningState);
                stateManager.attach(kubusScreenState);
                System.out.println("Switching to kubus world...");
            }

            if(name.equals("Pause") && !isPressed) {

                if (stateManager.hasState(gameRunningState) && gameRunningState.isEnabled()) {
                    gameRunningState.setEnabled(paused);
                    stateManager.attach(pauseScreenState);
                    System.out.println("Pausing game...");

                } else if (stateManager.hasState(pauseScreenState)) {
                    stateManager.detach(pauseScreenState);
                    gameRunningState.setEnabled(true);
                    System.out.println("Trying to run again...");
                }
            }
        }
    };

    @Override
    public void destroy() {
        System.exit(0);
    }
}

