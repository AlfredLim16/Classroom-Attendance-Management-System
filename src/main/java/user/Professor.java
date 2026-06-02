package user;

import java.util.Objects;

public record Professor(int professorId, User user, String firstName, String middleName, String lastName, ProfessorType professorType) {

    public Professor{
        if(user == null){
            throw new IllegalArgumentException("user is required");
        }
        if(firstName == null || firstName.isBlank()){
            throw new IllegalArgumentException("firstName is required");
        }
        if(lastName == null || lastName.isBlank()){
            throw new IllegalArgumentException("lastName is required");
        }
        if(professorType == null){
            throw new IllegalArgumentException("professorType is required");
        }
    }

    public String getFullName(){
        return firstName + (middleName != null ? " " + middleName : "") + " " + lastName;
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object == null || getClass() != object.getClass()){
            return false;
        }
        Professor professor = (Professor) object;
        return professorId == professor.professorId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(professorId);
    }

    @Override
    public String toString(){
        return "Professor{id=" + professorId + ", name='" + getFullName() + "', type=" + professorType + "}";
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int professorId;
        private User user;
        private String firstName;
        private String middleName;
        private String lastName;
        private ProfessorType professorType;

        public Builder professorId(int professorId){
            this.professorId = professorId;
            return this;
        }

        public Builder user(User user){
            this.user = user;
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

        public Builder professorType(ProfessorType professorType){
            this.professorType = professorType;
            return this;
        }

        public Professor build(){
            return new Professor(professorId, user, firstName, middleName, lastName, professorType);
        }
    }
}