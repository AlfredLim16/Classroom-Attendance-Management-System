package lookup;

import exceptions.ValidationException;

public enum DecisionType {
    MAKEUP(1, "Allow Make-up"), ZERO(2, "Zero Score"), EXCUSED_ABSENCE(3, "Excused Absence");

    private final int decisionTypeId;
    private final String decisionTypeName;

    DecisionType(int decisionTypeId, String decisionTypeName) {
        this.decisionTypeId = decisionTypeId;
        this.decisionTypeName = decisionTypeName;
    }

    public int getDecisionTypeId() {
        return decisionTypeId;
    }

    public String getDecisionTypeName() {
        return decisionTypeName;
    }

    public static DecisionType fromId(int id) throws ValidationException {
        for (DecisionType d : values()) {
            if (d.decisionTypeId == id) {
                return d;
            }
        }
        throw new ValidationException("decisionTypeId", "Unknown decision type id: " + id);
    }
}
