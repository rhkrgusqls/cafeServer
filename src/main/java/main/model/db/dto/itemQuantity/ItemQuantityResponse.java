package main.model.db.dto.itemQuantity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemQuantityResponse {
    private int itemId;
    private String name;
    private String category;
    private int quantity;

    // getters, setters
}