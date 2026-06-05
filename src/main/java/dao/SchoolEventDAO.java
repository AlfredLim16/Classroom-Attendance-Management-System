package dao;

import core.SchoolEvent;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface SchoolEventDAO {
    void insert(SchoolEvent event) throws SQLException, DuplicateEntryException;
    void update(SchoolEvent event) throws SQLException, NotFoundException;
    void delete(int eventId) throws SQLException, NotFoundException;
    SchoolEvent findById(int eventId) throws SQLException, NotFoundException;
    List<SchoolEvent> findAll() throws SQLException;
}
