package testgame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

public class GameApp extends SimpleApplication {
    
    private BulletAppState bulletAppState;
    private InputHandler inputHandler;
    private Player player;
    private Terrain terrain;
    private Hud hud;
    private Goal goal;
    private float boostTime = 1.0f;
    private float time = 0f;
    private boolean gameOn = false;
    private boolean gameFinished = false;
    
    @Override
    public void simpleInitApp() {
        // add physics to the game
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // add input handling
        inputHandler = new InputHandler();
        inputHandler.init(inputManager);

        // create player
        player = new Player(assetManager, cam.getLocation());
        player.attach(rootNode, bulletAppState.getPhysicsSpace());


        // create terrain
        terrain = new Terrain(assetManager);
        terrain.attachAndAddLodControl(rootNode, bulletAppState.getPhysicsSpace(), getCamera());

        // create hud
        hud = new Hud(assetManager);
        hud.attach(guiNode);

        // create goal
        goal = new Goal(new Vector3f(-50.30026f, -72f, 245.44533f), assetManager);
        goal.attach(rootNode, bulletAppState.getPhysicsSpace());
        
        setDisplayFps(false);
        setDisplayStatView(false);

        // attach collisionlistener
        bulletAppState.getPhysicsSpace().addCollisionListener(new GameChangingPhysicsListener(this));

        setUpCams();
        
        // ready to roll!
        System.out.println("INIT DONE!");
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
    }
    
    @Override
    public void simpleUpdate(float secondsFromLastTick) {
        System.out.println(player.getLocation());
        if (!gameOn) {
            updateHud();
            return;
        }
        
        if(player.getLocation().getY() < -150f) {
            player.setLocation(new Vector3f(0f, -20f, 0f));
            player.multiplyVelocity(0.0f);
            gameOn = false;
        }
        
        time += secondsFromLastTick;
        
        handleInput(secondsFromLastTick);
        updateHud();
    }
    
    private void handleInput(float secondsFromLastTick) {
        if (inputHandler.UP) {
            player.changeLinearVelocityTowards(cam.getDirection(), secondsFromLastTick * 1.5f);
        }
        
        if (inputHandler.RIGHT) {
            player.changeLinearVelocityTowards(cam.getLeft().mult(-1f), secondsFromLastTick * 1.5f);
        }
        
        if (inputHandler.LEFT) {
            player.changeLinearVelocityTowards(cam.getLeft(), secondsFromLastTick * 1.5f);
        }
        
        if (inputHandler.DOWN) {
            player.changeLinearVelocityTowards(cam.getDirection().mult(-1f), secondsFromLastTick * 1.5f);
        }
        
        if (inputHandler.SPACE && boostTime > 0) {
            player.setFriction(false);
            
            player.increaseVelocity(secondsFromLastTick);
            boostTime -= secondsFromLastTick;
            
            if (boostTime < 0) {
                boostTime = 0;
            }
        } else {
            player.setFriction(true);
            
            boostTime += secondsFromLastTick * 0.1f;
            if (boostTime > 1.0f) {
                boostTime = 1.0f;
            }
        }
        
        if (player.getVelocity() > 40f) {
            player.multiplyVelocity(0.95f);
        }
    }
    
    private void updateHud() {
        if (gameFinished) {
            hud.setTimeText("Time: " + String.format("%.3f", time) + " !!!");
            hud.setVelocityText("");
            hud.setBoostText("Well done!");
            player.multiplyVelocity(0.95f);
            return;
        }
        
        hud.setBoostText("Boost: " + String.format("%.0f", boostTime * 100));
        hud.setVelocityText("Velocity: " + String.format("%.0f", player.getVelocity() * 8) + " km/h");
        hud.setTimeText("Time: " + String.format("%.3f", time));
    }
    
    private void setUpCams() {
        flyCam.setEnabled(false);
        
        ChaseCamera chaseCam = new ChaseCamera(cam, player.getGeometry(), inputManager);
        
        chaseCam.setTrailingEnabled(true);
        chaseCam.setSmoothMotion(false);
        chaseCam.setChasingSensitivity(150f);
        chaseCam.setRotationSensitivity(255f);
        chaseCam.setTrailingSensitivity(255f);
        chaseCam.setDefaultDistance(8f);
        chaseCam.setMaxDistance(10f);
        chaseCam.setMinDistance(7f);
        chaseCam.setLookAtOffset(new Vector3f(0f, 0.5f, 0f));
    }
    
    public void gameOn() {
        if (!gameFinished) {
            gameOn = true;
        }
    }
    
    public void finish() {
        gameFinished = true;
        gameOn = false;
    }
}
