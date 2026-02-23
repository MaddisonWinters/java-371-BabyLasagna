package cs.BabyLasagna.GameObj;

import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent {

    private static final float DEFAULT_GRAVITY = -25f;

    private final TransformComponent transform;

    private final Vector2 velocity = new Vector2();
    private final Vector2 acceleration = new Vector2();

    private float gravity = DEFAULT_GRAVITY;

    private boolean grounded = false;

    public PhysicsComponent(TransformComponent transform) {
        this.transform = transform;
    }

    public void update(float delta) {

        // Apply gravity only if not grounded
        if (!grounded) {
            acceleration.y = gravity;
        } else {
            acceleration.y = 0;
        }

        // Integrate velocity
        velocity.mulAdd(acceleration, delta);

        // Integrate position
        transform.position.mulAdd(velocity, delta);

        // Reset horizontal acceleration each frame (input should reapply it)
        acceleration.x = 0;
    }

    public void addHorizontalForce(float force) {
        acceleration.x += force;
    }

    public void setVerticalVelocity(float value) {
        velocity.y = value;
    }

    public void stopHorizontal() {
        velocity.x = 0;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;

        if (grounded && velocity.y < 0) {
            velocity.y = 0;
        }
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}
