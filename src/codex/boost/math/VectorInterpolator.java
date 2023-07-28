/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost.math;

import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public interface VectorInterpolator {
    
    public static final VectorInterpolator
            LERP = (a, b, c) -> b.subtract(a).multLocal(c);
    public static final VectorInterpolator
            CONSTANT = (a, b, c) -> {
                if (a.distanceSquared(b) < c*c) return b;
                return b.subtract(a).normalizeLocal().multLocal(c);
            };
    public static final VectorInterpolator
            INSTANT = (a, b, c) -> b;
    
    public Vector3f getNextVector(Vector3f a, Vector3f b, float c);
    
}
