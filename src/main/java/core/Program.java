package core;

import validations.ProgramValidator;
import java.util.Objects;

public record Program(int programId, String programName) {

    public Program {
        ProgramValidator.validate(programName);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Program program = (Program) object;
        return programId == program.programId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(programId);
    }

    @Override
    public String toString() {
        return "Program{id=" + programId + ", name='" + programName + "'}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int programId;
        private String programName;

        public Builder programId(int programId) {
            this.programId = programId;
            return this;
        }

        public Builder programName(String programName) {
            this.programName = programName;
            return this;
        }

        public Program build() {
            return new Program(programId, programName);
        }
    }
}
