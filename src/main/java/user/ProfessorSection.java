package user;

import course.Section;
import course.Semester;

public record ProfessorSection(
    int professorSectionId,
    Professor professor,
    Section section,
    Semester semester,
    boolean professorRecording
    ) {

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int professorSectionId;
        private Professor professor;
        private Section section;
        private Semester semester;
        private boolean professorRecording = false;

        public Builder professorSectionId(int professorSectionId){
            this.professorSectionId = professorSectionId;
            return this;
        }

        public Builder professor(Professor professor){
            this.professor = professor;
            return this;
        }

        public Builder section(Section section){
            this.section = section;
            return this;
        }

        public Builder semester(Semester semester){
            this.semester = semester;
            return this;
        }

        public Builder professorRecording(boolean professorRecording){
            this.professorRecording = professorRecording;
            return this;
        }

        public ProfessorSection build(){
            return new ProfessorSection(professorSectionId, professor, section, semester, professorRecording);
        }
    }
}
