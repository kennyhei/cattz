package game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import com.jme3.system.AppSettings;
import game.GUI.Tonegod;
import game.appstates.GameScreenState;
import game.appstates.KubusScreenState;
import game.appstates.PauseScreenState;
import game.appstates.StartScreenState;
import game.managers.LevelManager;
import game.models.CoordinateHelper;

public class Main extends SimpleApplication {


    /* States */
    private GameScreenState gameRunningState;
    private StartScreenState startScreenState;
    private KubusScreenState kubusScreenState;
    private PauseScreenState pauseScreenState;
    private Tonegod tonegod;

    /* Level manager */
    private LevelManager levelManager;

    private boolean switchToKubus = false;
    private boolean isRunning = false;
    private static Main APPLICATION;

    private Main() {
        APPLICATION = this;
    }

    public static Main getApp() {
        return APPLICATION;
    }

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
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        flyCam.setEnabled(false);

        // Create GUI
        tonegod = new Tonegod(this);

        // Create level manager
        levelManager = new LevelManager();

        // Create states
        startScreenState = new StartScreenState(this, tonegod);
        
        stateManager.attach(startScreenState);
        inputManager.clearRawInputListeners();
        
        // inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Continue", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        //  inputManager.addListener(actionListener, "Start");
        inputManager.addListener(actionListener, "Continue");
        inputManager.addListener(actionListener, "Pause");

        // Tonegodgui click listener
        // inputManager.addMapping("level", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "level");

        new CoordinateHelper().attachCoordinates(Vector3f.ZERO, rootNode);
    }

    public void setSwitch(boolean switchOk) {
        this.switchToKubus = switchOk;
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {


            if (name.equals("level")) {
                // we know something has been clicked
                gameRunningState = new GameScreenState();

                System.out.println("KLIKKKK");
                stateManager.detach(startScreenState);
                stateManager.attach(gameRunningState);
                gameRunningState.setEnabled(true);
                System.out.println("level 1");


            }

//            if (name.equals("Start") && !isPressed && !isRunning) {
//                stateManager.detach(startScreenState);
//                stateManager.attach(gameRunningState);
//                gameRunningState.setEnabled(true);
//                isRunning = true;
//                System.out.println("Starting game...");
//            }

            if (name.equals("Continue") && !isPressed && switchToKubus && !stateManager.hasState(kubusScreenState)) {
                
                kubusScreenState = new KubusScreenState();
                stateManager.detach(gameRunningState);
                stateManager.attach(kubusScreenState);
                System.out.println("Switching to kubus world...");
            }

            if (name.equals("Pause") && !isPressed) {
                pauseScreenState = new PauseScreenState();

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

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setActiveLevel(int levelIndex) {
        getLevelManager().setActiveLevel(levelIndex);
        actionListener.onAction("level", true, 5);
    }

    @Override
    public void destroy() {
        System.exit(0);
    }
}
