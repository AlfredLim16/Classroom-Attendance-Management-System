package lookup;

import exceptions.ValidationException;

public enum Role {
    ADMIN(1, "Admin"), PROFESSOR(2, "Professor"), STUDENT(3, "Student"), SECRETARY(4, "Secretary");

    private final int roleId;
    private final String roleName;

    Role(int roleId, String roleName){
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getRoleId(){
        return roleId;
    }

    public String getRoleName(){
        return roleName;
    }

    public static Role fromId(int id) throws ValidationException {
        for (Role role : values()) {
            if (role.roleId == id) {
                return role;
            }
        }
        throw new ValidationException("roleId", "Unknown role id: " + id);
    }
}
