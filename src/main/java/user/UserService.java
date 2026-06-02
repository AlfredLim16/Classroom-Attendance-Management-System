package user;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User createUser(String userName, String userPassword, user.Role role){
        User user = new User(0, userName, userPassword, role);
        return userDAO.save(user);
    }

    public Optional<User> getUserById(int id){
        return userDAO.findById(id);
    }

    public List<User> getAllUsers(){
        return userDAO.findAll();
    }

    public boolean updateUser(User user){
        return userDAO.update(user);
    }

    public boolean deleteUser(int id){
        return userDAO.deleteById(id);
    }

    public Optional<User> login(String userName, String userPassword){
        Optional<User> userOpt = userDAO.findByUserName(userName);
        if(userOpt.isPresent() && userOpt.get().userPassword().equals(userPassword)){
            return userOpt;
        }
        return Optional.empty();
    }
}
