/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.connection;

/**
 *
 * @author codex
 * @param <T> connecting object type
 * @param <R> key type
 */
public interface Provider <T, R> {
    
    public T fetchConnectingObject(R key);
    
}
