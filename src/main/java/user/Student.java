package user;

import course.Program;
import course.Section;
import course.YearLevel;

public record Student(int studentId, User user, String studentNumber, String firstName, String middleName, String lastName, Program program, YearLevel yearLevel, Section section) {

    public String getFullName(){
        return firstName + (middleName != null ? " " + middleName : "") + " " + lastName;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int studentId;
        private User user;
        private String studentNumber;
        private String firstName;
        private String middleName;
        private String lastName;
        private Program program;
        private YearLevel yearLevel;
        private Section section;

        public Builder studentId(int studentId){
            this.studentId = studentId;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public Builder studentNumber(String studentNumber){
            this.studentNumber = studentNumber;
            return this;
        }

        public Builder firstName(String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder middleName(String middleName){
            this.middleName = middleName;
            return this;
        }

        public Builder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder program(Program program){
            this.program = program;
            return this;
        }

        public Builder yearLevel(YearLevel yearLevel){
            this.yearLevel = yearLevel;
            return this;
        }

        public Builder section(Section section){
            this.section = section;
            return this;
        }

        public Student build(){
            return new Student(studentId, user, studentNumber, firstName, middleName, lastName, program, yearLevel, section);
        }
    }
}
