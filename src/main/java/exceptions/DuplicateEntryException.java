package exceptions;

public class DuplicateEntryException extends AttendanceSystemException {

    private final String entityType;
    private final String duplicateField;
    private final Object duplicateValue;

    public DuplicateEntryException(String entityType, String duplicateField, Object duplicateValue) {
        super(entityType + " already exists with " + duplicateField + ": " + duplicateValue);
        this.entityType = entityType;
        this.duplicateField = duplicateField;
        this.duplicateValue = duplicateValue;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getDuplicateField() {
        return duplicateField;
    }

    public Object getDuplicateValue() {
        return duplicateValue;
    }

    @Override
    public String toString() {
        return "DuplicateEntryException{entityType='" + entityType + "', field='" + duplicateField + "', value=" + duplicateValue + "}";
    }
}
