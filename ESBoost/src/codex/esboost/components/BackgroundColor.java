/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class BackgroundColor implements EntityComponent {
    
    private final ColorRGBA color = new ColorRGBA();

    public BackgroundColor(ColorRGBA color) {
        this.color.set(color);
    }

    public ColorRGBA getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "BackgroundColor{" + "color=" + color + '}';
    }
    
}
