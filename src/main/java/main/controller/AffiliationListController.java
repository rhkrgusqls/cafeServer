package main.controller;

import main.exception.DeleteAffiliationException;
import main.exception.SignupException;
import main.model.db.dto.affiliationList.AffiliationListDTO;
import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.affiliationList.AffiliationListResponse;
import main.model.db.dto.signup.SignUpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//종결

@RestController
@RequestMapping("/affiliation")
public class AffiliationListController {

    @Autowired
    private AffiliationDAO affiliationDAO;

    @GetMapping("/list")
    public AffiliationListResponse getAffiliationList() {
        try {
            List<AffiliationListDTO> list = affiliationDAO.getAllAffiliationList();
            return new AffiliationListResponse(list);
        } catch (Exception e) {
            return new AffiliationListResponse(Collections.emptyList());
        }
    }

    @DeleteMapping("/delete")
    public String deleteAffiliation(@RequestParam String affiliationCode) {
        try {
            affiliationDAO.deleteAffiliation(affiliationCode);
            return "삭제가 완료되었습니다.";
        } catch (DeleteAffiliationException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }
}
