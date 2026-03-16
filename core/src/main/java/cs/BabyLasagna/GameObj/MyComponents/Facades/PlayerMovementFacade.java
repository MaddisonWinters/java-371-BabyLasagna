package cs.BabyLasagna.GameObj.MyComponents.Facades;

import cs.BabyLasagna.GameObj.MyComponents.CoyoteTimeComponent;
import cs.BabyLasagna.GameObj.MyComponents.FastFallingComponent;
import cs.BabyLasagna.GameObj.MyComponents.JumpBufferComponent;
import cs.BabyLasagna.GameObj.MyComponents.StateControllerComponent;
import cs.BabyLasagna.GameObj.UIHandler;

public class PlayerMovementFacade {

    private CoyoteTimeComponent coyoteTime;
    private FastFallingComponent fastFall;
    private JumpBufferComponent jumpBuffer;

    public PlayerMovementFacade() {
        coyoteTime = new CoyoteTimeComponent(0.12f);
        fastFall = new FastFallingComponent(2.0f);
        jumpBuffer = new JumpBufferComponent(0.12f);
    }

}
