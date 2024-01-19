/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 * @param <T>
 */
public interface Factory <T> {
    
    public T create(String name, EntityId customer);
    
}
