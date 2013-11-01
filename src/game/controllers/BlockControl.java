package game.controllers;

import game.models.HudBlock;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BlockControl extends AbstractControl {

    private ColorRGBA color;
    private HudBlock hudBlock;

    public BlockControl(ColorRGBA color) {
        this.color = color;
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void setHudBlock(HudBlock hudBlock) {
        this.hudBlock = hudBlock;
    }

    public HudBlock getHudBlock() {
        return hudBlock;
    }

    public ColorRGBA getColor() {
        return color;
    }
}