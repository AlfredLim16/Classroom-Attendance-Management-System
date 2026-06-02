package course;

import java.util.List;
import java.util.Optional;

public class ProgramService {

    private final ProgramDAO programDAO = new ProgramDAO();

    public Program createProgram(String programName){
        Program program = new Program(0, programName);
        return programDAO.save(program);
    }

    public Optional<Program> getProgramById(int id){
        return programDAO.findById(id);
    }

    public List<Program> getAllPrograms(){
        return programDAO.findAll();
    }

    public boolean updateProgram(Program program){
        return programDAO.update(program);
    }

    public boolean deleteProgram(int id){
        return programDAO.deleteById(id);
    }
}
