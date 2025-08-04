package main.model.db.dto.itemQuantity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemQuantityRequest {
    private int itemId;
    private String affiliationCode;

    // getters, setters
}