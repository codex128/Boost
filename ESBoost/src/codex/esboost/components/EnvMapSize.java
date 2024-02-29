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
public class EnvMapSize implements EntityComponent {
    
    private final int size;

    public EnvMapSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
    
}
