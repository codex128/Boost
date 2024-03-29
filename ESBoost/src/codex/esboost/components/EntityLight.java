/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.FunctionFilter;
import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class EntityLight implements EntityComponent {
    
    public static final int
            DIRECTIONAL = 0,
            POINT = 1,
            SPOT = 2,
            AMBIENT = 3,
            PREBUILT_PROBE = 4,
            RUNTIME_PROBE = 5;
    
    private final int type;
    private final ColorRGBA color;

    public EntityLight(int type) {
        this(type, ColorRGBA.White);
    }
    public EntityLight(int type, ColorRGBA color) {
        this.type = type;
        this.color = color;
    }

    public int getType() {
        return type;
    }
    public ColorRGBA getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return "EntityLight{" + "type=" + type + '}';
    }
    
    public static FunctionFilter<EntityLight> filter(int type) {
        return new FunctionFilter<>(EntityLight.class, c -> c.type == type);
    }
    public static FunctionFilter<EntityLight> filter(int... types) {
        return new FunctionFilter<>(EntityLight.class, c -> {
            for (int t : types) if (t == c.type) {
                return true;
            }
            return false;
        });
    }
    public static int valueOf(String lightType) {
        switch (lightType.toLowerCase()) {
            case "directional":     return DIRECTIONAL;
            case "point":           return POINT;
            case "spot":            return SPOT;
            case "ambient":         return AMBIENT;
            case "probe": // to keep compatibility
            case "prebuilt-probe":  return PREBUILT_PROBE;
            case "runtime-probe":   return RUNTIME_PROBE;
            default:                return AMBIENT;
        }
    }
    
}
