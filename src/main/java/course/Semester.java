package course;

import java.time.LocalDate;
import java.util.Objects;

public record Semester(
    int semesterId,
    String semesterName,
    String schoolYear,
    LocalDate startDate,
    LocalDate endDate
    ) {

    public Semester{
        if(semesterName == null || semesterName.isBlank()){
            throw new IllegalArgumentException("semesterName is required");
        }
        if(schoolYear == null || schoolYear.isBlank()){
            throw new IllegalArgumentException("schoolYear is required");
        }
        if(startDate == null){
            throw new IllegalArgumentException("startDate is required");
        }
        if(endDate == null){
            throw new IllegalArgumentException("endDate is required");
        }
        if(endDate.isBefore(startDate)){
            throw new IllegalArgumentException("endDate must not be before startDate");
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
        Semester semester = (Semester) object;
        return semesterId == semester.semesterId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(semesterId);
    }

    @Override
    public String toString(){
        return "Semester{id=" + semesterId + ", name='" + semesterName + "', sy='" + schoolYear + "'}";
    }
}
