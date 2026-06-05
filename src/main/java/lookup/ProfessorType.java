package lookup;

import exceptions.ValidationException;

public enum ProfessorType {
    FACULTY(1, "Faculty"), FULL_TIME(2, "Full-time"), PART_TIME(3, "Part-time");

    private final int professorTypeId;
    private final String professorTypeName;

    ProfessorType(int professorTypeId, String professorTypeName){
        this.professorTypeId = professorTypeId;
        this.professorTypeName = professorTypeName;
    }

    public int getProfessorTypeId(){
        return professorTypeId;
    }

    public String getProfessorTypeName(){
        return professorTypeName;
    }

    public static ProfessorType fromId(int id) throws ValidationException {
        for (ProfessorType p : values()) {
            if (p.professorTypeId == id) {
                return p;
            }
        }
        throw new ValidationException("professorTypeId", "Unknown professor type id: " + id);
    }
}
