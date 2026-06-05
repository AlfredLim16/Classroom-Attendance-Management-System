package core;

import validations.SchoolEventValidator;
import java.time.LocalDate;
import java.util.Objects;

public record SchoolEvent(int eventId, String eventName, LocalDate eventDate) {

    public SchoolEvent {
        SchoolEventValidator.validate(eventName, eventDate);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        SchoolEvent that = (SchoolEvent) object;
        return eventId == that.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "SchoolEvent{id=" + eventId + ", name='" + eventName + "', date=" + eventDate + "}";
    }
}
