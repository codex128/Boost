/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package codex.boost;

/**
 *
 * @author gary
 */
public enum Motion {
    /**
     * Instantly move to the ideal location.
     */
    INSTANT,
    /**
     * Move a constant speed straight toward the ideal location.
     */
    LINEAR,
    /**
     * Smooth motion toward the ideal location.
     */
    LERP;
}
