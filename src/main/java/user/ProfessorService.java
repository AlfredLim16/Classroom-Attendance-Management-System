package user;

import java.util.List;
import java.util.Optional;

public class ProfessorService {

    private final ProfessorDAO professorDAO = new ProfessorDAO();

    public Professor createProfessor(User user, String firstName, String middleName, String lastName, ProfessorType professorType){
        Professor professor = new Professor(0, user, firstName, middleName, lastName, professorType);
        return professorDAO.save(professor);
    }

    public Optional<Professor> getProfessorById(int id){
        return professorDAO.findById(id);
    }

    public List<Professor> getAllProfessors(){
        return professorDAO.findAll();
    }

    public List<Professor> getProfessorsByType(ProfessorType type){
        return professorDAO.findByProfessorType(type);
    }

    public Optional<Professor> getProfessorByUserId(int userId){
        return professorDAO.findByUserId(userId);
    }

    public boolean updateProfessor(Professor professor){
        return professorDAO.update(professor);
    }

    public boolean deleteProfessor(int id){
        return professorDAO.deleteById(id);
    }
}
