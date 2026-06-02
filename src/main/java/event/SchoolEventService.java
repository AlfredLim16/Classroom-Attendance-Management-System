package event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SchoolEventService {

    private final SchoolEventDAO schoolEventDAO = new SchoolEventDAO();

    public SchoolEvent createSchoolEvent(String eventName, LocalDate eventDate){
        SchoolEvent event = new SchoolEvent(0, eventName, eventDate);
        return schoolEventDAO.save(event);
    }

    public Optional<SchoolEvent> getSchoolEventById(int id){
        return schoolEventDAO.findById(id);
    }

    public List<SchoolEvent> getAllSchoolEvents(){
        return schoolEventDAO.findAll();
    }

    public List<SchoolEvent> getSchoolEventsByDate(LocalDate date){
        return schoolEventDAO.findByEventDate(date);
    }

    public boolean updateSchoolEvent(SchoolEvent event){
        return schoolEventDAO.update(event);
    }

    public boolean deleteSchoolEvent(int id){
        return schoolEventDAO.deleteById(id);
    }
}
