/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.connection;

/**
 *
 * @author codex
 * @param <T>
 * @param <R>
 */
public interface Connector <T, R> {
    
    /**
     * Indicates which objects support multiple connections.
     */
    public enum MultiConnectionHint {
        
        /**
         * Neither object supports multiple connections.
         */
        None(false, false),
        
        /**
         * Only object A supports multiple connections.
         */
        OnlyObjectA(true, false),
        
        /**
         * Only object B supports multiple connections.
         */
        OnlyObjectB(false, true),
        
        /**
         * Both objects support multiple connections.
         */
        All(true, true);
        
        private final boolean a, b;

        private MultiConnectionHint(boolean a, boolean b) {
            this.a = a;
            this.b = b;
        }
        
        public boolean isASupport() {
            return a;
        }
        public boolean isBSupport() {
            return b;
        }
        public boolean fullSupport() {
            return a && b;
        }
        public boolean supportsMultipleConnections(boolean isObjectA) {
            return (a && isObjectA) || (b && !isObjectA);
        }
        
    }
    
    /**
     * Connects the two objects together.
     * 
     * @param objectA
     * @param objectB 
     */
    public void connect(T objectA, R objectB);
    
    /**
     * Disconnects the two objects.
     * 
     * @param objectA
     * @param objectB 
     */
    public void disconnect(T objectA, R objectB);
    
    /**
     * Gets the multiple connection hint which determines which
     * set of objects supports connections with multiple objects
     * of the other set.
     * 
     * @return 
     */
    public MultiConnectionHint getMultiConnectionHint();
    
    /**
     * Determines if the two given A objects are equal.
     * 
     * @param object1
     * @param object2
     * @return 
     */
    public default boolean compareAObjects(T object1, Object object2) {
        return object1 == object2;
    }
    
    /**
     * Determines if the two given B objects are equal.
     * 
     * @param object1
     * @param object2
     * @return 
     */
    public default boolean compareBObjects(R object1, Object object2) {
        return object1 == object2;
    }
    
}
