package course;

import java.util.Objects;

public record Section(
    int sectionId,
    Program program,
    YearLevel yearLevel,
    String sectionCode
    ) {

    public Section{
        if(program == null){
            throw new IllegalArgumentException("program is required");
        }
        if(yearLevel == null){
            throw new IllegalArgumentException("yearLevel is required");
        }
        if(sectionCode == null || sectionCode.isBlank()){
            throw new IllegalArgumentException("sectionCode is required");
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
        Section section = (Section) object;
        return sectionId == section.sectionId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(sectionId);
    }

    @Override
    public String toString(){
        return "Section{id=" + sectionId + ", code='" + sectionCode + "', program=" + (program != null ? program.programName() : "null") + "}";
    }
}
