/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityId;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class DesignFactory implements Factory<Design> {
    
    private final HashMap<String, Assembler<Design>> designs = new HashMap<>();
    
    @Override
    public Design create(String name, EntityId customer) {
        Assembler<Design> a = designs.get(name);
        if (a != null) {
            return a.assemble();
        } else {
            return null;
        }
    }
    
    public void add(String name, Assembler<Design> assembler) {
        designs.put(name, assembler);
    }
    
}
