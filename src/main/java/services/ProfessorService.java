package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lookup.ProfessorType;

public class ProfessorService {

    private final ProfessorDAO professorDAO;
    private final UserDAO userDAO;

    public ProfessorService(){
        UserDAO uDAO = new UserDAOImpl();
        this.userDAO = uDAO;
        this.professorDAO = new ProfessorDAOImpl(uDAO);
    }

    public Professor createProfessor(User user, String firstName, String middleName, String lastName, ProfessorType type){
        try{
            Professor professor = Professor.builder()
                .user(user).firstName(firstName).middleName(middleName)
                .lastName(lastName).professorType(type).build();
            professorDAO.insert(professor);
            return professorDAO.findByUserId(user.userId());
        }catch(SQLException | DuplicateEntryException | NotFoundException e){
            System.err.println("[ProfessorService] createProfessor: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProfessor(Professor professor){
        try{
            professorDAO.update(professor);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[ProfessorService] updateProfessor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProfessor(int professorId){
        try{
            professorDAO.delete(professorId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[ProfessorService] deleteProfessor: " + e.getMessage());
            return false;
        }
    }

    public Optional<Professor> getProfessorByUserId(int userId){
        try{
            return Optional.of(professorDAO.findByUserId(userId));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public List<Professor> getAllProfessors(){
        try{
            return professorDAO.findAll();
        }catch(SQLException e){
            System.err.println("[ProfessorService] getAllProfessors: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
