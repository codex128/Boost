/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author codex
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FactoryDesign {
    
    public static final String DEFAULT = "DefaultGroup";
    
    public String group() default DEFAULT;
    
}
