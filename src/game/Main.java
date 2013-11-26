package game;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.system.AppSettings;
import game.GUI.Tonegod;
import game.appstates.GameScreenState;
import game.appstates.KubusScreenState;
import game.appstates.PauseScreenState;
import game.appstates.StartScreenState;
import game.controllers.InputHandler;
import game.managers.LevelManager;


public class Main extends SimpleApplication {
    
    /* test public var */
    public static int level = 0;

    /* States */
    private GameScreenState gameRunningState;
    private StartScreenState startScreenState;
    private KubusScreenState kubusScreenState;
    private PauseScreenState pauseScreenState;
    private Tonegod tonegod;

    /* Level manager */
    private LevelManager levelManager;

    /* Input handler */
    private InputHandler inputHandler;

    private boolean switchToKubus = false;
    private boolean isRunning = false;

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

        // Create GUI
        tonegod = new Tonegod(this);

        // Create level manager
        levelManager = new LevelManager(this);

        // Create states
        gameRunningState = new GameScreenState(this);
        startScreenState = new StartScreenState(this, tonegod);
        kubusScreenState = new KubusScreenState(this);
        pauseScreenState = new PauseScreenState(this);

        stateManager.attach(startScreenState);

        this.inputHandler = new InputHandler();
        this.inputHandler.init(inputManager);

       // inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Continue", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_F1));
      //  inputManager.addListener(actionListener, "Start");
        inputManager.addListener(actionListener, "Continue");
        inputManager.addListener(actionListener, "Pause");

        // Tonegodgui click listener
       // inputManager.addMapping("level", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "level");
    }

    public void setSwitch(boolean switchOk) {
        this.switchToKubus = switchOk;
    }

    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {

            
            if (name.equals("level") && level == 1) {
               // we know something has been clicked
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

    public InputHandler getInputHandler() {
        return inputHandler;
        
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }
    
    public static void changeGameState(Main main) {
        level = 1;
        System.out.println(level);   
        main.actionListener.onAction("level", true, 5);
     
    }

    @Override
    public void destroy() {
        System.exit(0);
    }
}

