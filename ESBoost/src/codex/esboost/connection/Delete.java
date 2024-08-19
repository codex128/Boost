/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.connection;

import codex.esboost.connection.Connection;

/**
 *
 * @author codex
 */
public interface Delete {
    
    /**
     * Returns true if the given connection should be deleted.
     * 
     * @param c
     * @return 
     */
    public boolean delete(Connection c);
    
    public static Delete containing(Object object) {
        return new DeleteContaining(object);
    }
    public static Delete not(Connection connection) {
        return new DeleteOther(connection);
    }
    public static Delete and(Delete... requests) {
        return new DeleteIfAll(requests);
    }
    
    public static class DeleteContaining implements Delete {

        private final Object object;
        
        public DeleteContaining(Object object) {
            this.object = object;
        }
        
        @Override
        public boolean delete(Connection c) {
            return c.contains(object);
        }
        
    }
    public static class DeleteOther implements Delete {
        
        private final Connection connection;

        public DeleteOther(Connection connection) {
            this.connection = connection;
        }
        
        @Override
        public boolean delete(Connection c) {
            return c != connection;
        }
        
    }
    public static abstract class Compound implements Delete {
        
        protected final Delete[] requests;

        public Compound(Delete[] requests) {
            this.requests = requests;
        }
        
    }
    public static class DeleteIfAll extends Compound {

        public DeleteIfAll(Delete[] requests) {
            super(requests);
        }
        
        @Override
        public boolean delete(Connection c) {
            for (Delete r : requests) {
                if (!r.delete(c)) return false;
            }
            return true;
        }
        
    }
    
}
