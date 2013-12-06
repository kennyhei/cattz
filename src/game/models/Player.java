package game.models;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class Player implements ActionListener {
    
    public static String FORWARD = "Forward";
    public static String BACKWARD = "Backward";
    public static String LEFT = "Left";
    public static String RIGHT = "Right";
    public static String JUMP = "Jump";
    private CharacterControl playerControl;
    private Node playerNode;
    private Geometry playerModel;
    private Vector3f walkDirection;
    private Vector3f viewDirection;
    private boolean forward, backward, left, right;
    private Quaternion rotator = new Quaternion();
    
    public Player(AssetManager assetManager, Vector3f location) {
        playerNode = new Node("player");
        
        playerModel = (Geometry) assetManager.loadModel("Models/cat/cat.j3o");
        playerModel.scale(5f);
        
        playerControl = new CharacterControl(CollisionShapeFactory.createDynamicMeshShape(playerModel), 0.2f);
        playerControl.setJumpSpeed(20);
        playerControl.setFallSpeed(30);
        playerControl.setGravity(15);
        
        playerNode.addControl(playerControl);
        playerNode.attachChild(playerModel);
        
        playerNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        playerControl.setPhysicsLocation(location);
        playerNode.setLocalTranslation(location);
        
        walkDirection = playerControl.getWalkDirection();
        viewDirection = playerControl.getViewDirection();
    }
    
    public void update(float tpf) {
        walkDirection.set(0, 0, 0);
        if (left) {
            rotator.fromAngleAxis(tpf, Vector3f.UNIT_Y);
            viewDirection = rotator.mult(viewDirection);
        }
        if (right) {
            rotator.fromAngleAxis(-tpf, Vector3f.UNIT_Y);
            viewDirection = rotator.mult(viewDirection);
        }
        if (forward) {
            walkDirection.addLocal(viewDirection);
        }
        if (backward) {
            walkDirection.addLocal(viewDirection.negate());
        }
        
        if (walkDirection.y < 0) {
            walkDirection.y = 0.01f;
        }
        
        if (viewDirection.y < 0) {
            viewDirection.y = 0.01f;
        }
        
        
        playerControl.setWalkDirection(walkDirection);
        playerControl.setViewDirection(viewDirection);
    }
    
    public Node getPlayerNode() {
        return playerNode;
    }
    
    public PhysicsCharacter getPhysicsCharacter() {
        return playerControl;
    }
    
    public Geometry getModel() {
        return playerModel;
    }
    
    public void onAction(String name, boolean isPressed, float tpf) {
        System.out.println("action!: " + name);
        if (name.equals(FORWARD)) {
            forward = isPressed;
        }
        if (name.equals(BACKWARD)) {
            backward = isPressed;
        }
        if (name.equals(LEFT)) {
            left = isPressed;
        }
        if (name.equals(RIGHT)) {
            right = isPressed;
        }
        if (name.equals(JUMP)) {
            if (playerControl.onGround()) {
                playerControl.jump();
            }
        }
    }
}
