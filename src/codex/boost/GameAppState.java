/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.InputMapper;
import java.util.function.Consumer;

/**
 * Extension of {@link BaseAppState} that provides easier
 * access to important application fields.
 * 
 * @author codex
 */
public abstract class GameAppState extends BaseAppState {
    
    protected Application app;
    protected AssetManager assetManager;
    protected RenderManager renderManager;
    protected InputManager inputManager;
    protected InputMapper inputMapper;
    protected Camera cam;
    protected Vector3f windowSize;
    protected Node rootNode, guiNode;
    
    @Override
    protected void initialize(Application app) {
        this.app = app;
        AppSettings settings = app.getContext().getSettings();
        assetManager = app.getAssetManager();
        renderManager = app.getRenderManager();
        inputManager = app.getInputManager();
        cam = app.getCamera();
        windowSize = new Vector3f(settings.getWidth(), settings.getHeight(), 0f);
        if (GuiGlobals.getInstance() != null) {
            inputMapper = GuiGlobals.getInstance().getInputMapper();
        }
        if (app instanceof SimpleApplication) {
            SimpleApplication simpleApp = (SimpleApplication)app;
            rootNode = simpleApp.getRootNode();
            guiNode = simpleApp.getGuiNode();
        }
        init(app);
    }    
    protected abstract void init(Application app);
    
    protected <T extends AppState> T getState(Class<T> type, Consumer<T> event) {
        T state = getState(type);
        if (state != null) {
            event.accept(state);
        }
        return state;
    }
    
}
