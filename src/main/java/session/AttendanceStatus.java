package session;

public enum AttendanceStatus {
    PRESENT(1, "Present"),
    LATE(2, "Late"),
    ABSENT(3, "Absent"),
    EXCUSED(4, "Excused");

    private final int statusId;
    private final String statusName;

    AttendanceStatus(int statusId, String statusName){
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public int getStatusId(){
        return statusId;
    }

    public String getStatusName(){
        return statusName;
    }

    public static AttendanceStatus fromId(int id){
        for(AttendanceStatus s : values()){
            if(s.statusId == id){
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown statusId: " + id);
    }
}
