package testgame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;

public class Goal {

    private List<RigidBodyControl> goalPhysics;
    private List<Geometry> goalGeometries;
    public static final String NAME = "goal";

    public Goal(Vector3f location, AssetManager assetManager) {
        Material goalMaterial = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        goalMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/ballsy.png"));

        goalPhysics = new ArrayList<RigidBodyControl>();
        goalGeometries = new ArrayList<Geometry>();

        for (int y = 0; y < 6; y+=2) {
            for (int x = 0; x < 6; x+=2) {
                for (int z = 0; z < 6; z+=2) {
                    Box box = new Box(1f, 1f, 1f);

                    Geometry goalGeometry = new Geometry(Goal.NAME, box);
                    goalGeometry.setMaterial(goalMaterial);

                    RigidBodyControl body = new RigidBodyControl(1f);
                    goalGeometry.addControl(body);
                    
                    body.setPhysicsLocation(location);
                    body.setPhysicsLocation(body.getPhysicsLocation().add(x, y, z));
                    
                    goalPhysics.add(body);
                    goalGeometries.add(goalGeometry);
                }
            }
        }
    }

    public void attach(Node attachTo, PhysicsSpace physicsSpace) {
        for (Geometry geom : goalGeometries) {
            attachTo.attachChild(geom);
        }
        for (RigidBodyControl phys : goalPhysics) {
            physicsSpace.add(phys);
        }
    }
}
