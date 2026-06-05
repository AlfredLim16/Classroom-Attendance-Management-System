package core;

import validations.SemesterValidator;
import java.time.LocalDate;
import java.util.Objects;

public record Semester(int semesterId, String semesterName, String schoolYear, LocalDate startDate, LocalDate endDate) {

    public Semester {
        SemesterValidator.validate(semesterName, schoolYear, startDate, endDate);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Semester semester = (Semester) object;
        return semesterId == semester.semesterId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(semesterId);
    }

    @Override
    public String toString() {
        return "Semester{id=" + semesterId + ", name='" + semesterName + "', sy='" + schoolYear + "'}";
    }
}
