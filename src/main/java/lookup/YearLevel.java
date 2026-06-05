package lookup;

import exceptions.ValidationException;

public enum YearLevel {
    FIRST_YEAR(1, "1st Year"), SECOND_YEAR(2, "2nd Year"), THIRD_YEAR(3, "3rd Year"), FOURTH_YEAR(4, "4th Year");

    private final int yearLevelId;
    private final String yearLevelName;

    YearLevel(int yearLevelId, String yearLevelName) {
        this.yearLevelId = yearLevelId;
        this.yearLevelName = yearLevelName;
    }

    public int getYearLevelId() {
        return yearLevelId;
    }

    public String getYearLevelName() {
        return yearLevelName;
    }

    public static YearLevel fromId(int id) throws ValidationException {
        for (YearLevel y : values()) {
            if (y.yearLevelId == id) {
                return y;
            }
        }
        throw new ValidationException("yearLevelId", "Unknown year level id: " + id);
    }
}
