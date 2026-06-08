package services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * In-memory store that tracks which sectionIds have been delegated by a
 * professor to their secretary/class-officer for attendance recording.
 *
 * By default every section is NOT delegated — the professor takes attendance
 * themselves.  When the professor toggles "Allow Secretary", the sectionId is
 * added here, and the SecretaryAttendancePanel checks this store before
 * allowing any marks.
 *
 * This is per-session (not persisted to the database).  The toggle resets to
 * OFF whenever the application is restarted.
 */
public class AttendancePermissionStore {

    private static final AttendancePermissionStore INSTANCE = new AttendancePermissionStore();

    private final Set<Integer> delegatedSections = Collections.synchronizedSet(new HashSet<>());

    private AttendancePermissionStore() {}

    public static AttendancePermissionStore getInstance() {
        return INSTANCE;
    }

    /** Grant the secretary permission to record attendance for this section. */
    public void grantSecretary(int sectionId) {
        delegatedSections.add(sectionId);
    }

    /** Revoke secretary permission — professor will handle attendance. */
    public void revokeSecretary(int sectionId) {
        delegatedSections.remove(sectionId);
    }

    /** Returns true when the secretary is allowed to record for this section. */
    public boolean isSecretaryAllowed(int sectionId) {
        return delegatedSections.contains(sectionId);
    }

    /** Toggle the current state; returns the new state. */
    public boolean toggle(int sectionId) {
        if (isSecretaryAllowed(sectionId)) {
            revokeSecretary(sectionId);
            return false;
        } else {
            grantSecretary(sectionId);
            return true;
        }
    }
}
