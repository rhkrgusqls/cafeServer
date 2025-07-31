package main.controller;

import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.delAffilation.DeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/affiliation")
public class DelStore {

    @Autowired
    private AffiliationDAO affiliationDAO;

    @PostMapping("/delete")
    public String deleteAffiliation(@RequestBody DeleteRequest request) {
        int result = affiliationDAO.deleteAffiliation(request.getAffiliationCode());
        if (result == 1) {
            return "삭제가 완료되었습니다.";
        } else {
            return "삭제 실패";
        }
    }
}
