package user;

import course.Section;
import java.util.List;
import java.util.Optional;

public class SecretaryService {

    private final SecretaryDAO secretaryDAO = new SecretaryDAO();

    public Secretary createSecretary(Student student, Section section){
        Secretary secretary = new Secretary(0, student, section);
        return secretaryDAO.save(secretary);
    }

    public Optional<Secretary> getSecretaryById(int id){
        return secretaryDAO.findById(id);
    }

    public Optional<Secretary> getSecretaryByUserId(int userId){
        return secretaryDAO.findByUserId(userId);
    }

    public Optional<Secretary> getSecretaryByStudentNumber(String studentNumber){
        return secretaryDAO.findByStudentNumber(studentNumber);
    }

    public List<Secretary> getAllSecretaries(){
        return secretaryDAO.findAll();
    }

    public boolean updateSecretary(Secretary secretary){
        return secretaryDAO.update(secretary);
    }

    public boolean deleteSecretary(int id){
        return secretaryDAO.deleteById(id);
    }
}