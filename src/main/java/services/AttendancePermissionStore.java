package services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AttendancePermissionStore {

    private static final AttendancePermissionStore INSTANCE = new AttendancePermissionStore();

    private final Set<Integer> delegatedSections = Collections.synchronizedSet(new HashSet<>());

    private AttendancePermissionStore(){
    }

    public static AttendancePermissionStore getInstance(){
        return INSTANCE;
    }

    public void grantSecretary(int sectionId){
        delegatedSections.add(sectionId);
    }

    public void revokeSecretary(int sectionId){
        delegatedSections.remove(sectionId);
    }

    public boolean isSecretaryAllowed(int sectionId){
        return delegatedSections.contains(sectionId);
    }

    public boolean toggle(int sectionId){
        if(isSecretaryAllowed(sectionId)){
            revokeSecretary(sectionId);
            return false;
        }else{
            grantSecretary(sectionId);
            return true;
        }
    }
}
