package main.controller;

import main.model.db.dto.affiliationList.AffiliationListDTO;
import main.model.db.dao.AffiliationDAO;
import main.model.db.dto.affiliationList.AffiliationListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
        int result = affiliationDAO.deleteAffiliation(affiliationCode);
        if (result == 1) {
            return "삭제가 완료되었습니다.";
        } else {
            return "삭제 실패";
        }
    }
}
