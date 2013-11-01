/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class HudBlock {

    private Box blockBox;
    private Geometry blockGeometry;
    private ColorRGBA color;

    public HudBlock(AssetManager assetManager, ColorRGBA color, Vector3f location) {

        this.blockBox = new Box(8f, 13f, 1f);
        this.blockGeometry = new Geometry("Box", blockBox);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.LightGray);
        this.blockGeometry.setMaterial(mat);

        this.blockGeometry.setLocalTranslation(location);
        this.blockGeometry.rotate(0f, 0f, -0.4f);

        this.color = color;
    }

    public Geometry getGeometry() {
        return blockGeometry;
    }

    public void colour() {
        this.blockGeometry.getMaterial().setColor("Color", color);
    }
}
