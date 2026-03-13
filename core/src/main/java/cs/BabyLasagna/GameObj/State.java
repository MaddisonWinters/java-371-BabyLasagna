package cs.BabyLasagna.GameObj;

public interface State<T> {

    void enter(T entity);

    void update(T entity, float deltaTime);

    void exit(T entity);
}
