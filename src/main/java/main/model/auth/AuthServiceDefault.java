package main.model.auth;

import main.model.db.dao.UserDAO;
import main.model.db.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceDefault implements AuthService {

    private UserDAO userDAO;

    @Autowired
    AuthServiceDefault(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // 로그인용 메서드
    @Override
    public boolean login(String username, String password) {
        UserDTO user = userDAO.findById(username);
        if (user == null) {
            return false;
        }
        if (user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    // 로그인 확인용 메서드
    @Override
    public boolean authenticate(){
        return true;
    }

    // 회원가입용 메서드
    @Override
    public boolean signup(String userId , String password, String affiliationCode){
        UserDTO user = new UserDTO();
        user.setId(userId);
        user.setPassword(password);
        user.setAffiliationCode(affiliationCode);
        try {
            userDAO.insertUser(user);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
