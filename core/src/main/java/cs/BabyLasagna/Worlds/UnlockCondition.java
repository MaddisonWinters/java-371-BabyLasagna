package cs.BabyLasagna.Worlds;

import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class UnlockCondition {

    public abstract boolean isMet(Set<String> completedLevels);

    public static UnlockCondition fromJson(JsonValue json) {
        if (json == null) return new Always();
        return switch (json.getString("type")) {
            case "complete" -> new Complete(json.getString("level"));
            case "and"      -> new And(parseList(json.get("conditions")));
            case "or"       -> new Or(parseList(json.get("conditions")));
            default -> throw new IllegalArgumentException("Unknown unlock condition type: " + json.getString("type"));
        };
    }

    private static List<UnlockCondition> parseList(JsonValue arr) {
        List<UnlockCondition> list = new ArrayList<>();
        for (JsonValue child = arr.child; child != null; child = child.next) {
            list.add(fromJson(child));
        }
        return list;
    }

    public static class Always extends UnlockCondition {
        @Override public boolean isMet(Set<String> completedLevels) { return true; }
    }

    public static class Complete extends UnlockCondition {
        private final String levelId;
        public Complete(String levelId) { this.levelId = levelId; }
        @Override public boolean isMet(Set<String> completedLevels) { return completedLevels.contains(levelId); }
    }

    public static class And extends UnlockCondition {
        private final List<UnlockCondition> conditions;
        public And(List<UnlockCondition> conditions) { this.conditions = conditions; }
        @Override public boolean isMet(Set<String> completedLevels) {
            return conditions.stream().allMatch(c -> c.isMet(completedLevels));
        }
    }

    public static class Or extends UnlockCondition {
        private final List<UnlockCondition> conditions;
        public Or(List<UnlockCondition> conditions) { this.conditions = conditions; }
        @Override public boolean isMet(Set<String> completedLevels) {
            return conditions.stream().anyMatch(c -> c.isMet(completedLevels));
        }
    }
}
