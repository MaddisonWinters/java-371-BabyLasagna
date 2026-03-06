package cs.BabyLasagna.GameObj;

public class CoyoteTimeComponent {

    private float coyoteDuration = 0f;
    private float coyoteTimer = 0f;

    public CoyoteTimeComponent(float duration) {
        this.coyoteDuration = duration;
        this.coyoteTimer = 0f;
    }

    public float getCoyoteDuration() {
        return coyoteDuration;
    }

    public float getCoyoteTimer() {
        return coyoteTimer;
    }

    public void update(float delta, boolean isGrounded) {
        if (isGrounded) {
            coyoteTimer = coyoteDuration;
        } else {
            coyoteTimer = Math.max(0f, coyoteTimer - delta);
        }
    }

    public boolean canJump() {
        return coyoteTimer > 0f;
    }

    public void consume() {
        coyoteTimer = 0f;
    }

}
