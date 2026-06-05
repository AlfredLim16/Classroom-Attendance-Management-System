package core;

import lookup.YearLevel;
import validations.SectionValidator;
import java.util.Objects;

public record Section(int sectionId, Program program, YearLevel yearLevel, String sectionCode) {

    public Section {
        SectionValidator.validateSection(program, yearLevel, sectionCode);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Section section = (Section) object;
        return sectionId == section.sectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

    @Override
    public String toString() {
        return "Section{id=" + sectionId + ", code='" + sectionCode + "', program=" + (program != null ? program.programName() : "null") + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int sectionId;
        private Program program;
        private YearLevel yearLevel;
        private String sectionCode;

        public Builder sectionId(int sectionId) {
            this.sectionId = sectionId;
            return this;
        }

        public Builder program(Program program) {
            this.program = program;
            return this;
        }

        public Builder yearLevel(YearLevel yearLevel) {
            this.yearLevel = yearLevel;
            return this;
        }

        public Builder sectionCode(String sectionCode) {
            this.sectionCode = sectionCode;
            return this;
        }

        public Section build() {
            return new Section(sectionId, program, yearLevel, sectionCode);
        }
    }
}
