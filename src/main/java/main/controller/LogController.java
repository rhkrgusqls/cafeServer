package main.controller;

import main.log.db.dao.LogDAO;
import main.log.db.dto.ChangeLogDTO;
import main.log.db.dto.ConsumptionStatDTO;
import main.log.db.dto.ShipmentDTO;
import main.model.auth.AuthServiceSession;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    CustomProperties customProperties;

    @Autowired
    private AuthServiceSession authServiceSession;

    @Autowired
    private LogDAO logDAO;

    // 출고 리스트 조회 (점포 + 아이템)
    @GetMapping("/shipments")
    public List<ShipmentDTO> getShipments(
            @RequestParam(required = false) String affiliationCode,
            @RequestParam int itemId,
            @RequestParam(required = false, defaultValue = "day") String groupType  // 날짜 타입 추가, 기본값 day
    ) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();

        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        }
        if (affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = "*";
        }

        return logDAO.getShipmentsByAffiliationAndItem(affiliationCode, itemId, groupType);
    }

    // 소비량 조회 (점포 + 아이템)
    @GetMapping("/consumptions")
    public List<ConsumptionStatDTO> getConsumptions(
            @RequestParam(required = false, defaultValue = "day") String groupType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) String affiliationCode,
            @RequestParam int itemId
    ) {

        if (startDate == null) {
            startDate = LocalDate.of(2000, 1, 1);
        }

        String sessionAffiliationCode = authServiceSession.getSessionUser();

        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        }
        if (affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = "*";
        }

        Date sqlStartDate = java.sql.Date.valueOf(startDate);

        return logDAO.getConsumptionStatsByAffiliationAndItem(groupType, sqlStartDate, affiliationCode, itemId);
    }

    // 재고 변화 로그 조회 (점포 + 아이템)
    @GetMapping("/changes")
    public List<ChangeLogDTO> getInventoryChangeLogs(
            @RequestParam(required = false) String affiliationCode,
            @RequestParam int itemId
    ) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();

        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        } else if (!affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = sessionAffiliationCode;
        }

        return logDAO.getInventoryChangeLogsByAffiliationAndItem(affiliationCode, itemId);
    }

    @GetMapping("/inventory-breakdown")
    public List<Map<String, Integer>> getMonthlyInventoryBreakdown(
            @RequestParam String month,          // "2025-08" 같은 형식
            @RequestParam int itemId,
            @RequestParam(required = false) String affiliationCode
    ) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();

        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        } else if (!affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = sessionAffiliationCode;
        }

        return logDAO.getMonthlyInventoryBreakdown(month, itemId, Integer.parseInt(affiliationCode));
    }

}
