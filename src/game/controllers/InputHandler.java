package game.controllers;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class InputHandler implements ActionListener {

    /* Input keys */
    public boolean LEFT = false, RIGHT = false, UP = false, DOWN = false, SPACE = false;

    public void init(InputManager inputManager) {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) {
                LEFT = isPressed;
            }

            if (name.equals("Right")) {
                RIGHT = isPressed;
            }

            if (name.equals("Up")) {
                UP = isPressed;
            }

            if (name.equals("Down")) {
                DOWN = isPressed;
            }

            if (name.equals("Jump")) {
                SPACE = isPressed;
            }
    }
}
