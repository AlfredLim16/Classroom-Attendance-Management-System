package core;

import validations.SectionValidator;
import java.util.Objects;

public record Secretary(int secretaryId, Student student, Section section) {

    public Secretary {
        SectionValidator.validateSecretary(student, section);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Secretary that = (Secretary) object;
        return secretaryId == that.secretaryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secretaryId);
    }

    @Override
    public String toString() {
        return "Secretary{id=" + secretaryId + ", student=" + (student != null ? student.studentNumber() : "null") + ", section=" + (section != null ? section.sectionCode() : "null") + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int secretaryId;
        private Student student;
        private Section section;

        public Builder secretaryId(int secretaryId) {
            this.secretaryId = secretaryId;
            return this;
        }

        public Builder student(Student student) {
            this.student = student;
            return this;
        }

        public Builder section(Section section) {
            this.section = section;
            return this;
        }

        public Secretary build() {
            return new Secretary(secretaryId, student, section);
        }
    }
}
