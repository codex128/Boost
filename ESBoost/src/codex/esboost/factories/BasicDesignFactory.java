/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityId;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class BasicDesignFactory implements DesignFactory {
    
    private final HashMap<String, Assembler<Design>> designs = new HashMap<>();
    private DesignTools tools;
    
    public BasicDesignFactory() {}
    public BasicDesignFactory(GameSystemsState systems) {
        tools = wrapTools(systems);
    }
    
    private DesignTools wrapTools(GameSystemsState systems) {
        return new DesignTools(systems, this);
    }
    
    @Override
    public Design create(String name, EntityId customer) {
        Assembler<Design> a = designs.get(name);
        if (a != null) {
            Design d = a.assemble();
            if (tools != null) {
                d.setDesignTools(tools);
            }
            return d;
        } else {
            return null;
        }
    }    
    @Override
    public void register(String name, Assembler<Design> assembler) {
        designs.put(name, assembler);
    }
    
}
