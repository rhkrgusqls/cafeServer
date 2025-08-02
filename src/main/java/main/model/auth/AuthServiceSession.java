package main.model.auth;

import main.exception.LoginException;
import main.exception.SignupException;
import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.db.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

@Service("authServiceSession")
public class AuthServiceSession implements AuthService {

    private final HttpSession session;

    @Autowired
    AuthServiceSession(AffiliationDAO affiliationDAO, HttpSession session) {
        this.affiliationDAO = affiliationDAO;
        this.session = session;
    }

    private AffiliationDAO affiliationDAO;

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

        // 로그인 성공 시 세션에 사용자 정보 저장
        session.setAttribute("loginUser", affiliation);
        session.setAttribute("loginId", affiliation.getAffiliationCode());

        return true;
    }

    // 로그인 확인용 메서드
    @Override
    public boolean authenticate() {
        // 세션에서 로그인 정보 조회
        return true;
    }

    // 회원가입용 메서드
    @Override
    public boolean signup(String affiliationCode, String password, String storeName) {
        AffiliationDTO affiliation = new AffiliationDTO();
        affiliation.setAffiliationCode(affiliationCode);
        affiliation.setPassword(password);
        affiliation.setStoreName(storeName);
        try {
            affiliationDAO.insertAffiliation(affiliation);
            return true;
        } catch (Exception e) {
            throw new SignupException("점포 코드 중복 또는 데이터베이스 오류: " + affiliationCode, e);
        }
    }

    public void logout() {
        session.invalidate();
    }

    public String getSessionUser() {
        AffiliationDTO user = (AffiliationDTO) session.getAttribute("loginUser");
        return user.getAffiliationCode();
    }
}
