package cs.BabyLasagna.GameObj.MyComponents;

public class JumpBufferComponent {

    private float bufferDuration;
    private float bufferTimer;

    public JumpBufferComponent(float duration) {
        this.bufferDuration = duration;
        this.bufferTimer = 0f;
    }

    public void update(float delta) {
        bufferTimer = Math.max(0f, bufferTimer - delta);
    }

    public void recordJumpPress() {
        bufferTimer = bufferDuration;
    }

    public boolean hasBufferedJump() {
        return bufferTimer > 0f;
    }

    public void consume() {
        bufferTimer = 0f;
    }
}
