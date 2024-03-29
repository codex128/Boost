/*
 * $Id$
 * 
 * Copyright (c) 2015, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package codex.esboost;

import com.simsilica.es.*;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modified {@link EntityContainer} which provides access to more parameters.
 *
 * @author Paul Speed
 * @author codex
 * @param <T>
 */
public abstract class BoostEntityContainer<T> {

    static Logger log = LoggerFactory.getLogger(BoostEntityContainer.class);
    
    private EntityData ed;
    private ComponentFilter filter;
    private Class<? extends EntityComponent>[] componentTypes;
    private EntitySet entities;
    private T[] array;
    private Map<EntityId, T> objects = new HashMap<>();
    private Class parameter;
    
    @SuppressWarnings("unchecked")
    @SafeVarargs
    protected BoostEntityContainer( EntityData ed, Class<? extends EntityComponent>... componentTypes ) {
        this(ed, null, componentTypes);
    }
 
    @SuppressWarnings("unchecked")
    @SafeVarargs
    protected BoostEntityContainer( EntityData ed, ComponentFilter filter, Class<? extends EntityComponent>... componentTypes ) {
        this.ed = ed;
        this.filter = filter;
        this.componentTypes = componentTypes;
        
        // Find out what the type parameter is       
        for( Type t = getClass().getGenericSuperclass(); t != null; ) {
            if( t instanceof ParameterizedType ) {
                ParameterizedType pt = (ParameterizedType)t;
                if( pt.getRawType() == BoostEntityContainer.class ) {
                    if( pt.getActualTypeArguments()[0] instanceof ParameterizedType ) {
                        parameter = (Class)((ParameterizedType)pt.getActualTypeArguments()[0]).getRawType();
                    } else {
                        parameter = (Class)pt.getActualTypeArguments()[0];
                    }
                    break;
                }
            } else if( t instanceof Class ) {
                t = ((Class)t).getGenericSuperclass();
            } else {
                t = null;
            }
        }
    }

    protected void setFilter( ComponentFilter filter ) {
        if( this.filter == filter ) {
            return;
        }
        this.filter = filter;
        if( entities != null ) {
            entities.resetFilter(filter);
        }
    }

    protected EntityData getEntityData() {
        return ed;
    }
    
    @SuppressWarnings("unchecked")
    protected void addComponentTypes( Class<? extends EntityComponent>... add ) {
        Class[] merged = new Class[add.length + componentTypes.length];
        System.arraycopy(componentTypes, 0, merged, 0, componentTypes.length);
        System.arraycopy(add, 0, merged, componentTypes.length, add.length);
        this.componentTypes = merged;
    }
 
    public int size() {
        return entities.size();
    }

    public T getObject( EntityId id ) {
        return objects.get(id);
    }
 
    @SuppressWarnings("unchecked")
    protected T[] getArray() {
        if( array != null ) {
            return array;
        }
        array = (T[])Array.newInstance(parameter, objects.size());
        array = objects.values().toArray(array);
        return array;
    }
    
    public EntitySet getSet() {
        return entities;
    }
 
    protected abstract T addObject( Entity e );
    
    protected abstract void lazyObjectUpdate( T object, Entity e );
    
    protected abstract void persistentObjectUpdate(T object, Entity e);
    
    protected abstract void removeObject( T object, Entity e );    
    
    protected void addObjects( Set<Entity> set ) {
        if( set.isEmpty() ) {
            return;
        }
        for( Entity e : set ) {
            T object = addObject(e);
            objects.put(e.getId(), object);
        }
        array = null;
    }
    
    protected void updateObjects( Set<Entity> set ) {
        if( set.isEmpty() ) {
            return;
        }
        for( Entity e : set ) {
            T object = objects.get(e.getId());
            if( object == null ) {
                log.warn("Update: No matching object for entity:" + e);
                continue;
            }
            lazyObjectUpdate(object, e);
        }
    }
    
    protected void removeObjects( Set<Entity> set ) {
        if( set.isEmpty() ) {
            return;
        }
        for( Entity e : set ) {
            T object = objects.remove(e.getId());
            if( object == null ) {
                log.warn("Remove: No matching object for entity:" + e);
                continue;
            }
            removeObject(object, e);
        }
        array = null;
    }
    
    public void start() {
        this.entities = ed.getEntities(filter, componentTypes);
        entities.applyChanges();
        addObjects(entities);   
    }
    
    public boolean update() {
        boolean u = entities.applyChanges();
        if (u) {
            removeObjects(entities.getRemovedEntities());
            addObjects(entities.getAddedEntities());
            updateObjects(entities.getChangedEntities());
        }
        for (Entity e : entities) {
            persistentObjectUpdate(getObject(e.getId()), e);
        }
        return u;
    }
    
    public void stop() {
        removeObjects(entities);
        this.entities.release();
        this.entities = null;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[components=" + Arrays.asList(componentTypes) + "]";
    }
}

