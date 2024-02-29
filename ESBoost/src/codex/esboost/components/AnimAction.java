/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.anim.AnimComposer;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class AnimAction implements EntityComponent {
    
    private final String layer, action;
    private final boolean loop;

    public AnimAction(String action) {
        this(AnimComposer.DEFAULT_LAYER, action, true);
    }
    public AnimAction(String layer, String action) {
        this(layer, action, true);
    }
    public AnimAction(String action, boolean loop) {
        this(AnimComposer.DEFAULT_LAYER, action, loop);
    }
    public AnimAction(String layer, String action, boolean loop) {
        this.layer = layer;
        this.action = action;
        this.loop = loop;
    }

    public String getLayer() {
        return layer;
    }
    public String getAction() {
        return action;
    }
    public boolean isLoop() {
        return loop;
    }

    @Override
    public String toString() {
        return "AnimAction{" + "layer=" + layer + ", action=" + action + ", loop=" + loop + '}';
    }
    
}
