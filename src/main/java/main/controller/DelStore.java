package main.controller;

import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.delAffilation.DeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/affiliation")
public class DelStore {

    @Autowired
    private AffiliationDAO affiliationDAO;

    @DeleteMapping("/delete")
    public String deleteAffiliation(@RequestParam String affiliationCode) {
        int result = affiliationDAO.deleteAffiliation(affiliationCode);
        if (result == 1) {
            return "삭제가 완료되었습니다.";
        } else {
            return "삭제 실패";
        }
    }
}
