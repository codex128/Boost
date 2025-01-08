/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Simple interface for managing listeners.
 * 
 * @author gary
 * @param <T> listener type
 */
public interface Listenable <T> {
	
	/**
	 * Get listener collection.
	 * @return 
	 */
	public abstract Collection<T> getListeners();
	
	/**
	 * Add a listener.
	 * @param listener 
	 */
	public default void addListener(T listener) {
		getListeners().add(listener);
	}
	/**
	 * Remove the given listener, if able.
	 * @param listener 
	 */
	public default void removeListener(T listener) {
		getListeners().remove(listener);
	}
	/**
	 * Remove all listeners.
	 */
	public default void clearAllListeners() {
		getListeners().clear();
	}
	
	/**
	 * Execute the consumer for all listeners.
	 * @param notify 
	 */
	public default void notifyListeners(Consumer<T> notify) {
		for (T listener : getListeners()) {
			notify.accept(listener);
		}
	}
	
}
