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

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    CustomProperties customProperties;

    @Autowired
    private AuthServiceSession authServiceSession;

    @Autowired
    private LogDAO logDAO;

    @GetMapping("/shipments")
    public List<ShipmentDTO> getShipments(@RequestParam(required = false) String affiliationCode) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();
        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        } else if (!affiliationCode.equals(customProperties.getAffiliationCode())) {
            // 세션 코드가 아닌 다른 점포코드 요청 시 세션 코드로 강제
            affiliationCode = sessionAffiliationCode;
        }
        return logDAO.getAllShipmentsByAffiliation(affiliationCode);
    }

    @GetMapping("/consumptions")
    public List<ConsumptionStatDTO> getConsumptions(
            @RequestParam String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) String affiliationCode
    ) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();
        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        } else if (!affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = sessionAffiliationCode;
        }
        Date sqlStartDate = java.sql.Date.valueOf(startDate);
        return logDAO.getConsumptionStatsByAffiliation(period, sqlStartDate, affiliationCode);
    }

    @GetMapping("/changes")
    public List<ChangeLogDTO> getInventoryChangeLogs(@RequestParam(required = false) String affiliationCode) {
        String sessionAffiliationCode = authServiceSession.getSessionUser();
        if (affiliationCode == null) {
            affiliationCode = sessionAffiliationCode;
        } else if (!affiliationCode.equals(customProperties.getAffiliationCode())) {
            affiliationCode = sessionAffiliationCode;
        }
        return logDAO.getAllInventoryChangeLogsByAffiliation(affiliationCode);
    }
}
