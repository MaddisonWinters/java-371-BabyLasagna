package cs.BabyLasagna.GameObj.MyComponents;

import cs.BabyLasagna.GameObj.State;

public class StateControllerComponent<T> {

    private T entity;
    private State<T> currentState;

    public StateControllerComponent(T entity, State<T> initialState) {
        this.entity = entity;
        this.currentState = initialState;

        if (currentState != null) {
            currentState.enter(entity);
        }
    }

    public State<T> getCurrentState() { return currentState; }

    public boolean isInState(Class<? extends State<T>> stateType) {
        return stateType.isInstance(currentState);
    }

    public void update(float deltaTime) {
        if (currentState != null) {
            currentState.update(entity, deltaTime);
        }
    }

    public void changeState(State<T> newState) {

        if (currentState != null) {
            currentState.exit(entity);
        }

        currentState = newState;

        if (currentState != null) {
            currentState.enter(entity);
        }
    }

    public State<T> getState() {
        return currentState;
    }
}
