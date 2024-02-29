/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.anim;

import com.jme3.anim.tween.AbstractTween;
import java.util.function.Consumer;

/**
 *
 * @author codex
 * @param <T>
 */
public class ConsumerTween <T> extends AbstractTween {
    
    private final T arg;
    private final Consumer<T> consumer;
    
    public ConsumerTween(T arg, Consumer<T> consumer) {
        super(0);
        this.arg = arg;
        this.consumer = consumer;
    }
    
    @Override
    protected void doInterpolate(double t) {
        consumer.accept(arg);
    }
    
}
