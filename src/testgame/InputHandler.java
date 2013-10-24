package testgame;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class InputHandler implements ActionListener {

    public boolean LEFT = false, RIGHT = false, UP = false, DOWN = false, SPACE = false;

    public void init(InputManager inputManager) {
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Space");

        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addListener(this, "Left");

        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addListener(this, "Right");

        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addListener(this, "Up");

        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "Down");
    }

    public void onAction(String actionName, boolean isPressed, float tpf) {
        if (actionName.equals("Space")) {
            SPACE = isPressed;
        }

        if (actionName.equals("Left")) {
            LEFT = isPressed;
        }

        if (actionName.equals("Right")) {
            RIGHT = isPressed;
        }

        if (actionName.equals("Up")) {
            UP = isPressed;
        }

        if (actionName.equals("Down")) {
            DOWN = isPressed;
        }
    }
}
