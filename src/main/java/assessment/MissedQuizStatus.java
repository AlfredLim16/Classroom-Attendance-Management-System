package assessment;

public enum MissedQuizStatus {
    PENDING(1, "Pending"),
    APPROVED(2, "Approved"),
    REJECTED(3, "Rejected"),
    EXCUSED(4, "Excused");

    private final int missedQuizStatusId;
    private final String missedQuizStatusName;

    MissedQuizStatus(int missedQuizStatusId, String missedQuizStatusName){
        this.missedQuizStatusId = missedQuizStatusId;
        this.missedQuizStatusName = missedQuizStatusName;
    }

    public int getMissedQuizStatusId(){
        return missedQuizStatusId;
    }

    public String getMissedQuizStatusName(){
        return missedQuizStatusName;
    }

    public static MissedQuizStatus fromId(int id){
        for(MissedQuizStatus m : values()){
            if(m.missedQuizStatusId == id){
                return m;
            }
        }
        throw new IllegalArgumentException("Unknown missedQuizStatusId: " + id);
    }
}
