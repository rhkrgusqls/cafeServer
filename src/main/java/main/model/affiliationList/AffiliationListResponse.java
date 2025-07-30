package main.model.affiliationList;

import main.model.db.dto.AffiliationDTO;
import java.util.List;

public class AffiliationListResponse {

    private List<AffiliationDTO> affiliationList;

    public AffiliationListResponse() {
    }

    public AffiliationListResponse(List<AffiliationDTO> affiliationList) {
        this.affiliationList = affiliationList;
    }

    public List<AffiliationDTO> getAffiliationList() {
        return affiliationList;
    }

    public void setAffiliationList(List<AffiliationDTO> affiliationList) {
        this.affiliationList = affiliationList;
    }
}
