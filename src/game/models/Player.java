package game.models;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Player {

    private Node playerModel;
    private CharacterControl playerPhysics;
    private Spatial kisu;

    public Player(AssetManager assetManager, Vector3f location) {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(3f, 4f);
        playerPhysics = new CharacterControl(capsule, 0.01f);
        
        
        // this is experimental code, did not work --Emilia
        //playerModel = (Node) assetManager.loadModel("Objects/cat/cat.j3o");
        // kisu = assetManager.loadModel("Objects/cat/cat.j3o");
       
        
        
        
        playerModel = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        playerModel.addControl(playerPhysics);

        playerPhysics.setPhysicsLocation(location);
        playerPhysics.setJumpSpeed(20);
        playerPhysics.setFallSpeed(30);
        playerPhysics.setGravity(30);
    }

    public CharacterControl getPhysics() {
        return playerPhysics;
    }

    public Node getModel() {
        return playerModel;
    }
 //   experimental code   
//    public Spatial getKisu() {
//        return kisu;
//    }
    
  
}
