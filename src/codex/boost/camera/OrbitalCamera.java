/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.camera;

import codex.boost.Motion;
import static codex.boost.Motion.LERP;
import codex.boost.math.FDomain;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.Axis;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;

/**
 * Third Person Perspective (TPP) camera.
 *
 * @author gary
 */
public class OrbitalCamera extends AbstractControl implements AnalogFunctionListener, StateFunctionListener {

    public static final String INPUT_GROUP = "orbital_cam_input_group";
    public static final FunctionId ROTATE_XZ = new FunctionId(INPUT_GROUP, "rotate_xz"),
            ROTATE_Y = new FunctionId(INPUT_GROUP, "rotate_y"),
            ZOOM = new FunctionId(INPUT_GROUP, "zoom");
    private static boolean inputInitialized = false;

    Camera cam;
    Vector3f locationbase = new Vector3f();
    Vector3f focusbase = new Vector3f();
    Vector3f focusOffset = new Vector3f();
    Vector3f locationOffset = new Vector3f();
    Vector3f up = new Vector3f(0f, 1f, 0f);
    FDomain verticleRange = new FDomain(0f, FastMath.PI / 2.1f);
    FDomain distanceRange = new FDomain(2f, 3f);
    float horizontalSpeed = FastMath.PI / 1f;
    float verticleSpeed = FastMath.PI / 1.5f;
    float camspeed = .25f;
    float horizontalAngle = 0f;
    float verticleAngle = verticleRange.applyConstrain(0f);
    float distance = distanceRange.applyConstrain(2f);
    Motion motion = Motion.INSTANT;
    boolean dynamicDistancing = false;
    boolean trailing = false;
    boolean locationBaseFrozen = false;
    boolean focusBaseFrozen = false;
    boolean camLocationFrozen = false;

    public OrbitalCamera(Camera cam) {
        setCamera(cam);
    }

    public OrbitalCamera(Camera cam, InputMapper im) {
        setCamera(cam);
        if (!inputInitialized) {
            initializeInputMappings(im);
        }
        listenForInput(im);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!locationBaseFrozen) {
            locationbase.set(calculateLocationBase());
        }
        if (!focusBaseFrozen) {
            focusbase.set(calculateFocusBase());
        }
        if (!camLocationFrozen) {
            cam.setLocation(calculateNextCameraPosition());
        }
        cam.lookAt(getFocusBase(), up);
        if (dynamicDistancing) {
            distance = calculateDistance();
        }
        distance = distanceRange.applyConstrain(distance);
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    @Override
    public void valueActive(FunctionId func, double value, double tpf) {
        if (!enabled) return;
        if (func == ROTATE_XZ) {
            rotateHorizontally((float)(value*tpf));
        }
        else if (func == ROTATE_Y) {
            rotateVertically(-(float)(value*tpf));
        }
    }
    @Override
    public void valueChanged(FunctionId func, InputState value, double tpf) {
        if (!enabled) return;
        if (func == ZOOM) {
            distance -= value.asNumber();
        }
    }

    /**
     * Rotates the camera around the spatial on the horizontal plane.
     *
     * @param factor
     */
    public void rotateHorizontally(float factor) {
        horizontalAngle = FDomain.RADIANS.applyWrap(horizontalAngle
                + horizontalSpeed * factor * FastMath.abs(FastMath.cos(verticleAngle)));
    }

    /**
     * Rotates the camera up and down.
     *
     * @param factor
     */
    public void rotateVertically(float factor) {
        verticleAngle = verticleRange.applyConstrain(verticleAngle + verticleSpeed * factor);
    }

    /**
     * Set the location of the camera. Often used to snap the camera location
     * immediately to the ideal camera location.
     *
     * @param location
     */
    public void setCameraLocation(Vector3f location) {
        cam.setLocation(location);
    }

    /**
     * Freeze all aspects.
     *
     * @param freeze
     */
    public void freeze(boolean freeze) {
        setFreezes(freeze, freeze, freeze);
    }

    /**
     * Freeze location base, focus base, and camera location.
     *
     * @param locbase
     * @param focusbase
     * @param camloc
     */
    public void setFreezes(boolean locbase, boolean focusbase, boolean camloc) {
        freezeLocationBase(locbase);
        freezeFocusBase(focusbase);
        freezeCameraLocation(camloc);
    }

    /**
     * Halts the location base from being updated. The camera will always be
     * anchored to the same location, no matter where the subject moves. The
     * camera is still able to orbit around the location base (unless the camera
     * location itself is frozen).
     *
     * @param freeze enable/disable frozen location base
     */
    public void freezeLocationBase(boolean freeze) {
        locationBaseFrozen = freeze;
    }

    /**
     * Halts the focus base location from being updated. The camera will always
     * look at that same location, no matter where the subject moves.
     *
     * @param freeze enable/disable frozen focus base
     */
    public void freezeFocusBase(boolean freeze) {
        focusBaseFrozen = freeze;
    }

    /**
     * Halts the camera location from being updated. The camera will always
     * remain in the same location, ignoring subject location and
     * horizontal/verticle rotation.
     *
     * @param freeze enable/disable frozen camera location
     */
    public void freezeCameraLocation(boolean freeze) {
        camLocationFrozen = freeze;
    }

    private float calculateDistance() {
        return distanceRange.applyConstrain(getRealDistance());
    }

    private Vector3f calculateNextCameraPosition() {
        Vector3f target = calculateIdealCameraPosition();
        switch (motion) {
            case INSTANT:
                return target;
            case LINEAR:
                return motionLinear(cam.getLocation(), target, camspeed);
            case LERP:
                return motionLerp(cam.getLocation(), target, camspeed);
            default:
                throw new UnsupportedOperationException("Unsupported motion type!");
        }
    }

    private Vector3f calculateLocationBase() {
        if (spatial == null) {
            return null;
        }
        return spatial.getWorldTranslation().add(locationOffset);
    }

    private Vector3f calculateFocusBase() {
        if (spatial == null) {
            return null;
        }
        return spatial.getWorldTranslation().add(focusOffset);
    }

    private static Vector3f motionLinear(Vector3f vec, Vector3f target, float speed) {
        if (vec.distance(target) < speed) {
            return target;
        }
        else {
            return target.subtract(vec).normalizeLocal().multLocal(speed);
        }
    }

    private static Vector3f motionLerp(Vector3f vec, Vector3f target, float scalar) {
        return target.subtract(vec).multLocal(scalar).addLocal(vec);
    }

    /**
     * Calculates the ideal location of the camera. Does not represent the true
     * location of the camera.
     *
     * @return
     */
    public Vector3f calculateIdealCameraPosition() {
        if (spatial == null) {
            return null;
        }
        Quaternion q1 = new Quaternion().fromAngleAxis(-horizontalAngle, up);
        Quaternion q2 = new Quaternion().fromAngleAxis(FastMath.HALF_PI - verticleAngle,
                new Quaternion().lookAt(up, Vector3f.UNIT_Y).getRotationColumn(0));
        Vector3f vec = q1.mult(q2).mult(up);
        return getLocationBase().add(vec.multLocal(distance));
    }

    /**
     * Gets the location base.
     *
     * @return
     */
    public Vector3f getLocationBase() {
        return locationbase;
    }

    /**
     * Gets the focus base.
     *
     * @return
     */
    public Vector3f getFocusBase() {
        return focusbase;
    }

    /**
     * Gets the current direction the camera is facing.
     *
     * @return
     */
    public Vector3f getCurrentDirection() {
        return cam.getDirection();
    }

    /**
     * Gets the real distance between the camera and the spatial.
     *
     * @return
     */
    public float getRealDistance() {
        if (spatial == null) {
            return 0f;
        }
        return cam.getLocation().distance(getLocationBase());
    }

    /**
     * Set both offset values (focus and location).
     *
     * @param vec
     */
    public void setOffsets(Vector3f vec) {
        setFocusOffset(vec);
        setLocationOffset(vec);
    }

    /**
     * The offset from the spatial the camera will look at. default=(0,0,0)
     *
     * @param vec
     */
    public void setFocusOffset(Vector3f vec) {
        focusOffset.set(vec);
    }

    /**
     * The offset from ideal location the camera will be. default=(0,0,0)
     *
     * @param vec
     */
    public void setLocationOffset(Vector3f vec) {
        locationOffset.set(vec);
    }

    /**
     * The up direction used in calculating the camera look direction.
     * default=(0,1,0)
     *
     * @param vec
     */
    public void setUpDirection(Vector3f vec) {
        up.set(vec);
        cam.lookAtDirection(cam.getDirection(), up);
    }

    /**
     * Set both rotation speeds (horizontal and verticle).
     *
     * @param speed
     */
    public void setRotationSpeeds(float speed) {
        setHorizontalRotationSpeed(speed);
        setVerticleRotationSpeed(speed);
    }

    /**
     * The speed at which the camera will orbit the spatial on the XZ plane.
     * default=PI/100
     *
     * @param speed
     */
    public void setHorizontalRotationSpeed(float speed) {
        horizontalSpeed = speed;
    }

    /**
     * The speed at which the camera will orbit the spatial on the Y axis.
     * default=PI/150
     *
     * @param speed
     */
    public void setVerticleRotationSpeed(float speed) {
        verticleSpeed = speed;
    }

    /**
     * The speed at which the camera moves toward the ideal location.
     * default=.25f
     *
     * @param speed
     */
    public void setCameraSpeed(float speed) {
        camspeed = speed;
    }

    /**
     * The horizontal angle (xz) the ideal location is at. default=0f
     *
     * @param angle
     */
    public void setHorizontalAngle(float angle) {
        horizontalAngle = angle;
    }

    /**
     * The verticle angle (y) the ideal location is at. default=0f
     *
     * @param angle
     */
    public void setVerticleAngle(float angle) {
        verticleAngle = verticleRange.applyConstrain(angle);
    }

    /**
     * The distance the camera is from the spatial. default=5f
     *
     * @param distance
     */
    public void setDistance(float distance) {
        this.distance = distanceRange.applyConstrain(distance);
    }

    /**
     * Set the camera this control is using.
     *
     * @param cam
     */
    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    /**
     * Sets the mode of motion the camera uses to move toward the ideal
     * location. default=<code>Motion.INSTANT</code>
     *
     * @param motion
     */
    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    /**
     * Enables dynamic distancing. Dynamic distancing continually updates the
     * ideal camera distance to match the real camera distance. default=false
     *
     * @param enable
     */
    public void setDynamicDistancingEnabled(boolean enable) {
        dynamicDistancing = enable;
    }

    /**
     * Enables trailing mode. Trailing mode disables the effect of horizontal
     * rotation. The camera will move straight at the spatial (plus location
     * offset). Does apply verticle rotation.
     *
     * default=false
     *
     * @param enable
     */
    @Deprecated
    public void setTrailingModeEnabled(boolean enable) {
        trailing = enable;
        trailing = false;
    }

    public Vector3f getFocusOffset() {
        return focusOffset;
    }

    public Vector3f getLocationOffset() {
        return locationOffset;
    }

    public Vector3f getUpDirection() {
        return up;
    }

    public Vector3f getCameraUpDirection() {
        return cam.getUp();
    }

    public Vector3f getPlanarCameraDirection() {
        Plane plane = new Plane(up, new Vector3f());
        return plane.getClosestPoint(cam.getDirection()).normalizeLocal();
    }

    public float getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public float getCameraSpeed() {
        return camspeed;
    }

    public float getHorizontalAngle() {
        return horizontalAngle;
    }

    public float getVerticleAngle() {
        return verticleAngle;
    }

    public float getDistance() {
        return distance;
    }

    /**
     * The domain of the verticle angle. default=[min:0f, max:PI/2]
     *
     * @return
     */
    public FDomain getVerticleAngleDomain() {
        return verticleRange;
    }

    /**
     * The domain of the camera distance. default=[min:3f, max:7f]
     *
     * @return
     */
    public FDomain getDistanceDomain() {
        return distanceRange;
    }

    public Camera getCamera() {
        return cam;
    }

    public Motion getMotion() {
        return motion;
    }

    public boolean dynamicDistancingEnabled() {
        return dynamicDistancing;
    }

    @Deprecated
    public boolean trailingModeEnabled() {
        return trailing;
    }

    public boolean locationBaseFrozen() {
        return locationBaseFrozen;
    }

    public boolean focusBaseFrozen() {
        return focusBaseFrozen;
    }

    public boolean cameraLocationFrozen() {
        return camLocationFrozen;
    }

    /**
     * Initialize input mappings for OrbitalCamera.
     *
     * @param im
     */
    public static void initializeInputMappings(InputMapper im) {
        im.map(ROTATE_XZ, Axis.MOUSE_X);
        im.map(ROTATE_Y, Axis.MOUSE_Y);
        im.map(ZOOM, Axis.MOUSE_WHEEL);
        inputInitialized = true;
    }

    public void listenForInput(InputMapper im) {
        im.addAnalogListener(this, ROTATE_XZ, ROTATE_Y);
        im.addStateListener(this, ZOOM);
    }

    public void removeFromInput(InputMapper im) {
        im.removeAnalogListener(this, ROTATE_XZ, ROTATE_Y);
        im.removeStateListener(this, ZOOM);
    }

}
