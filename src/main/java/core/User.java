package core;

import lookup.Role;
import validations.UserValidator;
import java.util.Objects;

public record User(int userId, String userName, String userPassword, Role role) {

    public User {
        UserValidator.validate(userName, userPassword, role);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        User that = (User) object;
        return userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", userName='" + userName + "', role=" + role + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int userId;
        private String userName;
        private String userPassword;
        private Role role;

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder userPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(userId, userName, userPassword, role);
        }
    }
}
