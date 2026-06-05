package exceptions;

public class NotFoundException extends AttendanceSystemException {

    private final String entityType;
    private final Object identifier;

    public NotFoundException(String entityType, Object identifier) {
        super(entityType + " not found with identifier: " + identifier);
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "NotFoundException{entityType='" + entityType + "', identifier=" + identifier + "}";
    }
}
