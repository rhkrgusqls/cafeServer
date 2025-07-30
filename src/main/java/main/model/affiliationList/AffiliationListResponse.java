package main.model.affiliationList;

import main.model.db.dto.AffiliationDTO;
import java.util.List;

public class AffiliationListResponse {

    private List<AffiliationListDTO> affiliationList;

    public AffiliationListResponse() {
    }

    public AffiliationListResponse(List<AffiliationListDTO> affiliationList) {
        this.affiliationList = affiliationList;
    }

    public List<AffiliationListDTO> getAffiliationList() {
        return affiliationList;
    }

    public void setAffiliationList(List<AffiliationListDTO> affiliationList) {
        this.affiliationList = affiliationList;
    }
}
