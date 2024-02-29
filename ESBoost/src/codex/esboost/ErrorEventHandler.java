/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.jme3.app.Application;
import com.simsilica.event.ErrorEvent;
import com.simsilica.event.EventBus;
import com.simsilica.event.EventListener;
import com.simsilica.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author codex
 */
public class ErrorEventHandler implements EventListener<ErrorEvent> {
    
    private final Application app;
    private boolean closeOnFatal;

    public ErrorEventHandler(Application app) {
        this(app, true);
    }
    public ErrorEventHandler(Application app, boolean closeOnFatal) {
        this.app = app;
        this.closeOnFatal = closeOnFatal;
    }
    
    @Override
    public void newEvent(EventType<ErrorEvent> type, ErrorEvent event) {
        if (type == ErrorEvent.fatalError) {
            System.err.println("A fatal error has occured:");
            event.getError().printStackTrace(System.err);
            if (closeOnFatal) {
                System.err.println("Shutting down application...");
                app.stop();
            }
        } else if (type == ErrorEvent.dispatchError) {
            System.err.println("Warning: An event dispatch error has occured.");
            event.getError().printStackTrace(System.err);
        }
    }
    
    public static ErrorEventHandler setup(Application app) {
        return setup(app, true);
    }
    public static ErrorEventHandler setup(Application app, boolean closeOnFatal) {
        ErrorEventHandler handler = new ErrorEventHandler(app, closeOnFatal);
        EventBus.addListener(ErrorEvent.fatalError, handler);
        EventBus.addListener(ErrorEvent.dispatchError, handler);
        return handler;
    }
    
}
