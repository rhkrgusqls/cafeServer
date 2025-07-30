package main.controller;

import main.model.db.dao.AffiliationDAO;
import main.model.affiliationList.AffiliationListResponse;
import main.model.db.dto.AffiliationDTO;
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
            List<AffiliationDTO> list = affiliationDAO.getAllAffiliationList();
            return new AffiliationListResponse(list);
        } catch (Exception e) {
            return new AffiliationListResponse(Collections.emptyList());
        }
    }
}
