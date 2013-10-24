package testgame;

// what a name :P
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

public class GameChangingPhysicsListener implements PhysicsCollisionListener {

    private GameApp gameApp;

    public GameChangingPhysicsListener(GameApp gameApp) {
        this.gameApp = gameApp;
    }

    public void collision(PhysicsCollisionEvent event) {
        // collision system feeds all collisions to the listener
        // will figure out a better way to limit collision detection to only
        // this object and the player

        // if player is not involved, ignore event
        if(!hasName(event, Player.NAME)) {
            return;
        }
        
        // when player bumps with terrain, start game
        if(hasName(event, Terrain.NAME)) {
            gameApp.gameOn();
        }
        
        // when player bumps with goal, finish game
        if(hasName(event, Goal.NAME)) {
            gameApp.finish();
        }
    }

    private boolean hasName(PhysicsCollisionEvent event, String name) {
        return event.getNodeA().getName().equals(name) || event.getNodeB().getName().equals(name);
    }
}
