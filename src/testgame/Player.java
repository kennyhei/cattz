package testgame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Player {

    private RigidBodyControl ballPhysics;
    private Geometry ballGeometry;
    private final float defaultFriction = 0f;
    private final float boostFriction = 0f;
    public static final String NAME = "player";

    public Player(AssetManager assetManager, Vector3f location) {

        Material ballTextureMaterial = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        ballTextureMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/ballsy.png"));

        Sphere ballBody = new Sphere(64, 64, 0.5f, true, false);
        ballBody.setTextureMode(Sphere.TextureMode.Projected);
        ballGeometry = new Geometry(Player.NAME, ballBody);
        ballGeometry.setMaterial(ballTextureMaterial);
        ballGeometry.setLocalTranslation(location);
        ballPhysics = new RigidBodyControl(1f);
        ballGeometry.addControl(ballPhysics);
        ballPhysics.setPhysicsLocation(new Vector3f(-10f, -10f, 0f));
        ballPhysics.setFriction(defaultFriction);
    }
    
    public Vector3f getLocation() {
        return ballPhysics.getPhysicsLocation();
    }
    
    public void setLocation(Vector3f location) {
        ballPhysics.setPhysicsLocation(location);
    }

    public void attach(Node attachTo, PhysicsSpace physicsSpace) {
        attachTo.attachChild(ballGeometry);
        physicsSpace.add(ballPhysics);
    }

    public Geometry getGeometry() {
        return ballGeometry;
    }

    public void increaseVelocity(float secondsFromLastTick) {
        ballPhysics.setLinearVelocity(ballPhysics.getLinearVelocity().mult(1.0f + secondsFromLastTick));
    }

    public void changeLinearVelocityTowards(Vector3f direction, float amount) {
        float speed = ballPhysics.getLinearVelocity().length();
        ballPhysics.setLinearVelocity(
                ballPhysics.getLinearVelocity().normalize()
                .add(direction.normalize().mult(amount))
                .normalize().mult(speed));
    }

    public float getVelocity() {
        return ballPhysics.getLinearVelocity().length();
    }

    public void multiplyVelocity(float multiplier) {
        ballPhysics.setLinearVelocity(ballPhysics.getLinearVelocity().mult(multiplier));
        ballPhysics.setAngularVelocity(ballPhysics.getAngularVelocity().mult(multiplier));
    }

    public void setFriction(boolean enabled) {
        if (enabled) {
            ballPhysics.setFriction(defaultFriction);
        } else {
            ballPhysics.setFriction(boostFriction);
        }
    }
}
