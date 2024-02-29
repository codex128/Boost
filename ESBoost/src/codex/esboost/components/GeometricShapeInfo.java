/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.bullet.GeometricShape;
import com.simsilica.es.EntityComponent;
import vhacd.VHACDParameters;
import vhacd4.Vhacd4Parameters;

/**
 *
 * @author codex
 */
public class GeometricShapeInfo implements EntityComponent {
    
    private final String type;
    private final VHACDParameters vhacd;
    private final Vhacd4Parameters vhacd4;
    
    public GeometricShapeInfo(GeometricShape shape) {
        this(shape.name(), null, null);
    }
    public GeometricShapeInfo(GeometricShape shape, VHACDParameters vhacd) {
        this(shape.name(), vhacd, null);
    }
    public GeometricShapeInfo(GeometricShape shape, Vhacd4Parameters vhacd4) {
        this(shape.name(), null, vhacd4);
    }
    private GeometricShapeInfo(String type, VHACDParameters vhacd, Vhacd4Parameters vhacd4) {
        this.type = type;
        this.vhacd = vhacd;
        this.vhacd4 = vhacd4;
    }
    
    public String getType() {
        return type;
    }
    public VHACDParameters getVhacd() {
        return vhacd;
    }
    public Vhacd4Parameters getVhacd4() {
        return vhacd4;
    }
    @Override
    public String toString() {
        return "GeometricShapeInfo{" + "type=" + type + '}';
    }
    
}
