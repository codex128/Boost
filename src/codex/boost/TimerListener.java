/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost;

/**
 *
 * @author gary
 */
public interface TimerListener {

    public abstract void onTimerFinish(Timer timer);

    public default void onTimerReset(Timer timer) {
    }

    public default void onTimerStart(Timer timer) {
    }

    public default void onTimerPause(Timer timer) {
    }

}
