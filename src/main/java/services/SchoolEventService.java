package services;

import core.SchoolEvent;
import dao.*;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SchoolEventService {

    private final SchoolEventDAO schoolEventDAO;

    public SchoolEventService(){
        this.schoolEventDAO = new SchoolEventDAOImpl();
    }

    public List<SchoolEvent> getAllSchoolEvents(){
        try{
            return schoolEventDAO.findAll();
        }catch(SQLException e){
            System.err.println("[SchoolEventService] getAllSchoolEvents: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<SchoolEvent> getSchoolEventById(int eventId){
        try{
            return Optional.of(schoolEventDAO.findById(eventId));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }
}
