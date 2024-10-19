/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

/**
 *
 * @author codex
 */
public interface DesignFactory extends Factory<Design> {
    
    public void register(String name, Assembler<Design> assembler);
    
    public default void registerGroup(String name, Object... args) {
        // Register all designs that are annotated with the group name.
        // Args are used to instantiate designs.
    }
    
}
