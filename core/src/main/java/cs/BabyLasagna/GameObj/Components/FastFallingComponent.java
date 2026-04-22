package cs.BabyLasagna.GameObj.Components;

public class FastFallingComponent {

    private float multiplier;

    public FastFallingComponent(float multiplier) {
        this.multiplier = multiplier;
    }

    public float apply(float velocityY, float gravity, float delta, boolean grounded, boolean pressingDown) {

        if (!grounded && pressingDown && velocityY < 0f) {
            return velocityY + gravity * (multiplier-1f) * delta;
        }

        return velocityY;
    }
}
