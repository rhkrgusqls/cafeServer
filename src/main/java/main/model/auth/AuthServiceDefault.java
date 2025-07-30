package main.model.auth;

import main.exception.LoginException;
import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authServiceDefault")
public class AuthServiceDefault implements AuthService {

    private AffiliationDAO affiliationDAO;

    @Autowired
    AuthServiceDefault(AffiliationDAO affiliationDAO) {
        this.affiliationDAO = affiliationDAO;
    }

    // 로그인용 메서드
    @Override
    public boolean login(String affiliationId, String password) {
        AffiliationDTO affiliation = affiliationDAO.findByCode(affiliationId);

        if (affiliation == null) {
            throw new LoginException("존재하지 않는 아이디입니다.");
        }

        if (!affiliation.getPassword().equals(password)) {
            throw new LoginException("비밀번호가 올바르지 않습니다.");
        }

        return true;
    }
    // 로그인 확인용 메서드
    @Override
    public boolean authenticate(){
        return true;
    }

    // 회원가입용 메서드
    @Override
    public boolean signup(String affiliationCode, String password, String storeName) {
        AffiliationDTO affiliation = new AffiliationDTO();
        affiliation.setAffiliationCode(affiliationCode);
        affiliation.setPassword(password);
        affiliation.setStoreName(storeName);  // 점포명 세팅
        try {
            affiliationDAO.insertAffiliation(affiliation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
