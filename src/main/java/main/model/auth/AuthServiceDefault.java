package main.model.auth;

import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceDefault implements AuthService {

    private AffiliationDAO affiliationDAO;

    @Autowired
    AuthServiceDefault(AffiliationDAO userDAO) {
        this.affiliationDAO = affiliationDAO;
    }

    // 로그인용 메서드
    @Override
    public boolean login(String affiliationId, String password) {
        AffiliationDTO affiliation = affiliationDAO.findByCode(affiliationId);
        if (affiliation == null) {
            return false;
        }
        if (affiliation.getPassword().equals(password)) {
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
    public boolean signup(String affiliationCode , String password){
        AffiliationDTO affiliation = new AffiliationDTO();
        affiliation.setAffiliationCode(affiliationCode);
        affiliation.setPassword(password);
        try {
            affiliationDAO.insertAffiliation(affiliation);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
