package course;

import java.util.Objects;

public record Program(int programId, String programName) {

    public Program{
        if(programName == null || programName.isBlank()){
            throw new IllegalArgumentException("programName is required");
        }
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object == null || getClass() != object.getClass()){
            return false;
        }
        Program program = (Program) object;
        return programId == program.programId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(programId);
    }

    @Override
    public String toString(){
        return "Program{id=" + programId + ", name='" + programName + "'}";
    }
}
