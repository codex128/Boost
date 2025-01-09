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
     * Move at constant speed straight toward the ideal location.
     * Name is not correct, use {@code CONSTANT} instead.
     */
    @Deprecated
    LINEAR,
    /**
     * Move at a constant speed straight toward the ideal location.
     */
    CONSTANT,
    /**
     * Smooth motion using linear interpolation toward the ideal location.
     */
    LERP;
}
