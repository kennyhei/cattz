/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.GUI;

import com.jme3.app.SimpleApplication;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author ehjelm
 */
public class Tonegod {
    
    private Screen screen;
    private SimpleApplication app;
    private Node guiNode;
    private int winCount = 0;
    private Window win;
    
    
    public Tonegod(SimpleApplication app) {
        this.app = app;
        this.guiNode = app.getGuiNode();
    }
    
    
 
    public final void createNewWindow(String someWindowTitle) {
         Window nWin = new Window(screen, "Window" + winCount, new Vector2f( 
                 (screen.getWidth()/2)-175, (screen.getHeight()/2)-100 )
         );
        nWin.setWindowTitle(someWindowTitle);
        screen.addElement(nWin);
        winCount++;
    }
    
    public void drawGui(){
        this.screen = new Screen(app);
        guiNode.addControl(screen);
        
        win = new Window(screen, "win", new Vector2f(15,15));
        screen.addElement(win);
        
         ButtonAdapter makeWindow = new ButtonAdapter(screen, "Btn1", new Vector2f(15, 55)) {
    
        
        
        @Override
        public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
            createNewWindow("New Window " + winCount);
            }
        };
        
        win.addChild(makeWindow);
    }
    
    
    public void destroyGui(){
        screen.removeElement(win);
    }
    
   
    
}
