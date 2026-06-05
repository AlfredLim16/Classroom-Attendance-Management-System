package lookup;

import exceptions.ValidationException;

public enum ExcuseStatus {
    PENDING(1, "Pending"), APPROVED(2, "Approved"), REJECTED(3, "Rejected");

    private final int excuseStatusId;
    private final String excuseStatusName;

    ExcuseStatus(int excuseStatusId, String excuseStatusName){
        this.excuseStatusId = excuseStatusId;
        this.excuseStatusName = excuseStatusName;
    }

    public int getExcuseStatusId(){
        return excuseStatusId;
    }

    public String getExcuseStatusName(){
        return excuseStatusName;
    }

    public static ExcuseStatus fromId(int id) throws ValidationException {
        for (ExcuseStatus e : values()) {
            if (e.excuseStatusId == id) {
                return e;
            }
        }
        throw new ValidationException("excuseStatusId", "Unknown excuse status id: " + id);
    }
}
