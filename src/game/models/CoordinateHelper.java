
package game.models;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import game.Main;

public class CoordinateHelper {
    
    public void attachCoordinates(Vector3f position, Node node) {
        attachCoordinateAxes(position, node);
    }
    
    private void attachCoordinateAxes(Vector3f pos, Node node) {
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        arrow.setLineWidth(4);
        putShape(arrow, ColorRGBA.Red, node).setLocalTranslation(pos);
        
        arrow = new Arrow(Vector3f.UNIT_Y);
        arrow.setLineWidth(4);
        putShape(arrow, ColorRGBA.Green, node).setLocalTranslation(pos);
        
        arrow = new Arrow(Vector3f.UNIT_Z);
        arrow.setLineWidth(4);
        putShape(arrow, ColorRGBA.Blue, node).setLocalTranslation(pos);
    }
    
    private Geometry putShape(Mesh shape, ColorRGBA color, Node node) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(Main.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        node.attachChild(g);
        return g;
    }
}
