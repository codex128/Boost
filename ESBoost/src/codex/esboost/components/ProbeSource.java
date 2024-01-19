/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;

/**
 * Holds a file path to a light probe asset.
 * 
 * @author codex
 */
public class ProbeSource implements EntityComponent {
    
    private final String path;

    public ProbeSource(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "ProbeSource{" + "path=" + path + '}';
    }
    
}
