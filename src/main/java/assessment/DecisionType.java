package assessment;

public enum DecisionType {
    MAKEUP(1, "Make-up Quiz/Lab"),
    EXEMPTED(2, "Exempted"),
    FAILED(3, "Failed"),
    EXCUSED_ABSENCE(4, "Excused Absence");

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

    public static DecisionType fromId(int id) {
        for (DecisionType d : values()) {
            if (d.decisionTypeId == id) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown decisionTypeId: " + id);
    }
}
