/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.audio;

import codex.boost.Listenable;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioContext;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioParam;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.AudioSource;
import com.jme3.audio.Filter;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class SFXSpeaker extends Node implements AudioSource, Listenable<SpeakerListener> {

    AudioModel model;
    AudioData data;
    int channel = -1;
    Filter dryfilter;
    Filter reverbfilter;
    AudioSource.Status status = AudioSource.Status.Stopped;
    AudioSource.Status pstat = AudioSource.Status.Stopped;
    Vector3f velocity = new Vector3f();
    Vector2f angles = new Vector2f(360f, 360f); // inner, outer
    Transform lastWorldTransform = new Transform();
    float volumeFactor = 1f;
    LinkedList<SpeakerListener> listeners = new LinkedList<>();

    public SFXSpeaker(AudioData data, AudioModel model) {
        this.data = data;
        this.model = model;
    }

    public SFXSpeaker(AssetManager assets, AudioModel model) {
        this(assets, model.getSourceFile(), model, true);
    }

    public SFXSpeaker(AssetManager assets, String name, AudioModel model) {
        this(assets, name, model, true);
    }

    public SFXSpeaker(AssetManager assets, AudioModel model, boolean cache) {
        this(assets, model.getSourceFile(), model, cache);
    }

    public SFXSpeaker(AssetManager assets, String name, AudioModel model, boolean cache) {
        data = model.toAudioData(assets);
        this.model = model;
    }

    public void playInstance() {
        if (isPositional() && !supportsPositional()) {
            throw new IllegalStateException("Positional audio only supports one channel!");
        }
        getRenderer().playSourceInstance(this);
    }

    public void play() {
        if (isPositional() && !supportsPositional()) {
            throw new IllegalStateException("Positional audio only supports one channel!");
        }
        getRenderer().playSource(this);
    }

    public void stop() {
        getRenderer().stopSource(this);
    }

    public void pause() {
        getRenderer().pauseSource(this);
    }

    @Override
    public void updateLogicalState(float tpf) {
        if (isPlaying()) {
            Transform world = getWorldTransform();
            if (!lastWorldTransform.getTranslation().equals(world.getTranslation())) {
                lastWorldTransform.setTranslation(world.getTranslation());
                updateSourceParam(AudioParam.Position);
            }
            if (!lastWorldTransform.getRotation().equals(world.getRotation())) {
                lastWorldTransform.setRotation(world.getRotation());
                updateSourceParam(AudioParam.Direction);
            }
        }
        update(tpf);
    }

    /**
     * Manually updates this speaker.
     * <p>
     * Is used to track audio status and notify listeners on change. Does not
     * need to be called if this speaker is attached to the scene graph.
     *
     * @param tpf
     */
    public void manualUpdate(float tpf) {
        if (getParent() == null) {
            update(tpf);
        }
    }

    protected void update(float tpf) {
        if (status != pstat) {
            if (null != status) {
                switch (status) {
                    case Playing:
                        notifyListeners(l -> l.onSpeakerPlay(this));
                        break;
                    case Paused:
                        notifyListeners(l -> l.onSpeakerPause(this));
                        break;
                    case Stopped:
                        notifyListeners(l -> l.onSpeakerStop(this));
                        break;
                }
            }
            pstat = status;
        }
    }

    protected AudioRenderer getRenderer() {
        AudioRenderer renderer = AudioContext.getAudioRenderer();
        if (renderer == null) {
            throw new IllegalStateException("No audio renderer available!");
        }
        return renderer;
    }

    public void updateSourceParam(AudioParam param) {
        if (isPlaying()) {
            getRenderer().updateSourceParam(this, param);
        }
    }

    public AudioModel getModel() {
        return model;
    }

    public boolean supportsPositional() {
        return data.getChannels() == 1;
    }

    public boolean isPlaying() {
        return channel >= 0;
    }

    public float getVolumeFactor() {
        return volumeFactor;
    }

    public void setDryFilter(Filter dryfilter) {
        this.dryfilter = dryfilter;
        updateSourceParam(AudioParam.DryFilter);
    }

    public void setReverbFilter(Filter reverbfilter) {
        this.reverbfilter = reverbfilter;
        updateSourceParam(AudioParam.ReverbFilter);
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
        updateSourceParam(AudioParam.Velocity);
    }

    public void setInnerAngle(float inner) {
        angles.x = inner;
        updateSourceParam(AudioParam.InnerAngle);
    }

    public void setOuterAngle(float outer) {
        angles.y = outer;
        updateSourceParam(AudioParam.OuterAngle);
    }

    public void setAngles(float inner, float outer) {
        setInnerAngle(inner);
        setOuterAngle(outer);
    }

    public void setVolumeFactor(float volFactor) {
        this.volumeFactor = volFactor;
        updateSourceParam(AudioParam.Volume);
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public Filter getDryFilter() {
        return dryfilter;
    }

    @Override
    public AudioData getAudioData() {
        return data;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isLooping() {
        return model.isLooping();
    }

    @Override
    public float getPitch() {
        return model.getPitch();
    }

    @Override
    public float getVolume() {
        return model.getVolume() * getVolumeFactor();
    }

    @Override
    public float getTimeOffset() {
        return model.getTimeOffset();
    }

    @Override
    public float getPlaybackTime() {
        if (channel >= 0) {
            return getRenderer().getSourcePlaybackTime(this);
        }
        else {
            return 0f;
        }
    }

    @Override
    public Vector3f getPosition() {
        return getWorldTranslation();
    }

    @Override
    public Vector3f getVelocity() {
        return velocity;
    }

    @Override
    public boolean isReverbEnabled() {
        return model.isReverb();
    }

    @Override
    public Filter getReverbFilter() {
        return reverbfilter;
    }

    @Override
    public float getMaxDistance() {
        return model.getMaxDistance();
    }

    @Override
    public float getRefDistance() {
        return model.getRefDistance();
    }

    @Override
    public boolean isDirectional() {
        return model.isDirectional();
    }

    @Override
    public Vector3f getDirection() {
        return getWorldRotation().getRotationColumn(2);
    }

    @Override
    public float getInnerAngle() {
        return angles.x;
    }

    @Override
    public float getOuterAngle() {
        return angles.y;
    }

    @Override
    public boolean isPositional() {
        return model.isPositional();
    }

    @Override
    public Collection<SpeakerListener> getListeners() {
        return listeners;
    }

}
