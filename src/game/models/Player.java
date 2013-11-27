package game.models;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Player {

    private Geometry playerModel;
    private CharacterControl playerPhysics;
    private CollisionShape capsule;
    

    public Player(AssetManager assetManager, Vector3f location) {
//        CapsuleCollisionShape capsule = new CapsuleCollisionShape(3f, 4f);
          
        
        playerModel = (Geometry) assetManager.loadModel("Models/cat/cat.j3o");
//        Material mat = new Material(assetManager, "Models/cat/cat_diff.tga");
//        cat.setMaterial(mat);
       // rootNode.attachChild(cat);
       // cat.move(player.getPhysics().getPhysicsLocation());
        
             
        playerModel.scale(10);
        capsule = CollisionShapeFactory.createDynamicMeshShape(playerModel);
        playerPhysics = new CharacterControl(capsule, 0.01f);
        //playerModel.move(0, -20, 0);
        //playerModel = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        //playerModel = (Node) assetManager.loadModel("Models/cat/cat.obj");
        playerModel.addControl(playerPhysics);

        playerPhysics.setPhysicsLocation(location);
        playerPhysics.setJumpSpeed(20);
        playerPhysics.setFallSpeed(30);
        playerPhysics.setGravity(30);
        
        
        
    }

    public CharacterControl getPhysics() {
        return playerPhysics;
    }

    public Geometry getModel() {
        return playerModel;
    }

    
  
}
