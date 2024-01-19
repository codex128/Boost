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
public class LightPower implements EntityComponent {
    
    private final float power;

    public LightPower(float power) {
        this.power = power;
    }
    public LightPower(double power) {
        this.power = (float)power;
    }

    public float getPower() {
        return power;
    }
    @Override
    public String toString() {
        return "Power{" + power + '}';
    }
    
}
