/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class ShadowInfo implements EntityComponent {
    
    private final boolean renderer;
    private final int resolution;
    private final int splits;

    public ShadowInfo(boolean renderer, int resolution) {
        this(renderer, resolution, 2);
    }
    public ShadowInfo(boolean renderer, int resolution, int splits) {
        this.renderer = renderer;
        this.resolution = resolution;
        this.splits = splits;
    }

    public boolean isRenderer() {
        return renderer;
    }
    public int getResolution() {
        return resolution;
    }
    public int getSplits() {
        return splits;
    }
    
}
