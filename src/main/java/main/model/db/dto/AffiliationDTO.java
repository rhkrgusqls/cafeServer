package main.model.db.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AffiliationDTO {
    private String affiliationCode;
    private String password;
    private String storeName;
}
