/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost.audio;

/**
 *
 * @author gary
 */
public interface SpeakerListener {
    
    public void onSpeakerPlay(SFXSpeaker speaker);
    public void onSpeakerPause(SFXSpeaker speaker);
    public void onSpeakerStop(SFXSpeaker speaker);
    
}
