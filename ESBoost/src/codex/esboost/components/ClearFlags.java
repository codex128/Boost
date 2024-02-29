/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.renderer.ViewPort;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class ClearFlags implements EntityComponent {
    
    private final boolean color, depth, stencil;

    public ClearFlags() {
        this(false, false, false);
    }
    public ClearFlags(ViewPort vp) {
        this(vp.isClearColor(), vp.isClearDepth(), vp.isClearStencil());
    }
    public ClearFlags(boolean color, boolean depth, boolean stencil) {
        this.color = color;
        this.depth = depth;
        this.stencil = stencil;
    }

    public boolean isColor() {
        return color;
    }
    public boolean isDepth() {
        return depth;
    }
    public boolean isStencil() {
        return stencil;
    }

    @Override
    public String toString() {
        return "ClearFlags{" + "color=" + color + ", depth=" + depth + ", stencil=" + stencil + '}';
    }
    
}
