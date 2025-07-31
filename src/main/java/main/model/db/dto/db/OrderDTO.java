package main.model.db.dto.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
public class OrderDTO {
    private int orderId;
    private int itemId;
    private int quantity;
    private String affiliationCode;
    private String state;
    private Timestamp orderDate;
    /** 대기상태 wait,
     * reviewing 검토중상태,
     * processed요청 처리됨상태(본점에서만 검토됨),
     * re-review-needed 재검토필요상태 ,
     * 종결상태 completed */
    public String getState() { return state; }

    /** 대기상태 wait,
     * reviewing 검토중상태,
     * processed요청 처리됨상태(본점에서만 검토됨),
     * re-review-needed 재검토필요상태 ,
     * 종결상태 completed */
    public void setState(String state) { this.state = state; }
}

