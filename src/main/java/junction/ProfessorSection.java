package junction;

import core.Professor;
import core.Section;
import core.Semester;
import validations.EnrollmentValidator;
import java.util.Objects;

public record ProfessorSection(int professorSectionId, Professor professor, Section section, Semester semester, boolean isProfessorRecording) {

    public ProfessorSection {
        EnrollmentValidator.validateProfessorSection(professor, section, semester);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ProfessorSection that = (ProfessorSection) object;
        return professorSectionId == that.professorSectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(professorSectionId);
    }

    @Override
    public String toString() {
        return "ProfessorSection{id=" + professorSectionId + ", professor=" + (professor != null ? professor.professorId() : "null") + ", section=" + (section != null ? section.sectionCode() : "null") + ", recording=" + isProfessorRecording + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int professorSectionId;
        private Professor professor;
        private Section section;
        private Semester semester;
        private boolean isProfessorRecording;

        public Builder professorSectionId(int professorSectionId) {
            this.professorSectionId = professorSectionId;
            return this;
        }

        public Builder professor(Professor professor) {
            this.professor = professor;
            return this;
        }

        public Builder section(Section section) {
            this.section = section;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder isProfessorRecording(boolean isProfessorRecording) {
            this.isProfessorRecording = isProfessorRecording;
            return this;
        }

        public ProfessorSection build() {
            return new ProfessorSection(professorSectionId, professor, section, semester, isProfessorRecording);
        }
    }
}
