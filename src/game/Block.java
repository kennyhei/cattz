package game;

// Kubus block

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Block {
    
    private Box blockBox;
    private Geometry blockGeometry;
    private Material blockMaterial;
    
    public Block(AssetManager assetManager, ColorRGBA color, Vector3f location) {
        
        // Create kubus block
        this.blockBox = new Box(2f, 4f, 1f);
        this.blockGeometry = new Geometry("Block", blockBox);
        this.blockGeometry.setLocalTranslation(location);
        
        this.blockMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.blockMaterial.setColor("Color", color);
        this.blockGeometry.setMaterial(blockMaterial);
    }
    
    public Geometry getBlockGeometry() {
        return blockGeometry;
    }
    
}
