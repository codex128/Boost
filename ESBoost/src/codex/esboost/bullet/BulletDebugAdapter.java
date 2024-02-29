/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import codex.boost.GameAppState;
import com.jme3.app.Application;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.bullet.debug.DebugConfiguration;
import com.jme3.renderer.ViewPort;
import com.simsilica.bullet.BulletSystem;
import com.simsilica.event.EventBus;
import com.simsilica.event.EventListener;
import com.simsilica.event.EventType;
import com.simsilica.sim.SimEvent;

/**
 *
 * @author codex
 */
public class BulletDebugAdapter extends GameAppState implements EventListener<SimEvent> {
    
    private final DebugConfiguration config;
    private BulletDebugAppState debugState;
    private ViewPort debugVp;
    
    public BulletDebugAdapter(DebugConfiguration config) {
        this.config = config;
        EventBus.addListener(SimEvent.simInitialized, this);
    }
    
    @Override
    protected void init(Application app) {
        debugVp = app.getRenderManager().createMainView("physics-debug", app.getCamera());
        config.setViewPorts(debugVp);
        if (debugState != null) {
            getStateManager().attach(debugState);
        }
    }
    @Override
    protected void cleanup(Application app) {
        if (debugState != null) {
            getStateManager().detach(debugState);
        }
        app.getRenderManager().removeMainView(debugVp);
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {}
    @Override
    public void newEvent(EventType<SimEvent> type, SimEvent event) {
        if (type == SimEvent.simInitialized) {
            BulletSystem bullet = event.getManager().get(BulletSystem.class);
            if (bullet != null) {
                config.setSpace(bullet.getSpace());
                debugState = new BulletDebugAppState(config);
                if (isInitialized()) {
                    getStateManager().attach(debugState);
                }
            }
        }
    }
    
    public static BulletDebugAdapter setup(boolean debug, Application app) {
        if (debug) {
            DebugConfiguration config = new DebugConfiguration();
            //config.setViewPorts(app.getViewPort());
            BulletDebugAdapter adapter = new BulletDebugAdapter(config);
            app.getStateManager().attach(adapter);
            return adapter;
        } else {
            return null;
        }
    }
    
}
