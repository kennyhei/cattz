package testgame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

public class Hud {

    private BitmapText boostText;
    private BitmapText velocityText;
    private BitmapText timeText;

    public Hud(AssetManager assetManager) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Console.fnt");
        boostText = createText(guiFont);
        boostText.setLocalTranslation(20, 20 + boostText.getLineHeight(), 0);

        velocityText = createText(guiFont);
        velocityText.setLocalTranslation(20, 20 + velocityText.getLineHeight() + boostText.getLineHeight(), 0);

        timeText = createText(guiFont);
        timeText.setLocalTranslation(20, 20 + velocityText.getLineHeight() + boostText.getLineHeight() + timeText.getHeight(), 0);
    }

    public void attach(Node guiNode) {
        guiNode.attachChild(boostText);
        guiNode.attachChild(velocityText);
        guiNode.attachChild(timeText);
    }

    public void setBoostText(String text) {
        boostText.setText(text);
    }

    public void setVelocityText(String text) {
        velocityText.setText(text);
    }

    public void setTimeText(String text) {
        timeText.setText(text);
    }

    private BitmapText createText(BitmapFont font) {
        BitmapText text = new BitmapText(font, false);

        text.setSize(font.getCharSet().getRenderedSize() * 2);
        text.setColor(ColorRGBA.Black);
        text.setShadowMode(RenderQueue.ShadowMode.Cast);
        text.setText("");

        return text;
    }
}
