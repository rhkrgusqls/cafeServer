package main.refresh;

import main.model.auth.AuthServiceSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private AuthServiceSession authServiceSession;

    private RefreshSubject adminSubject = new RefreshSubject();
    private RefreshSubject userSubject = new RefreshSubject();

    private Map<WebSocketSession, String> sessionAffiliation = new ConcurrentHashMap<>();
    private Map<WebSocketSession, RefreshObserver> sessionObserverMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("afterConnectionEstablished called - sessionId: " + session.getId());

        // ✅ affiliationCode를 쿼리 파라미터에서 파싱
        String affiliationCode = extractAffiliationCode(session.getUri());
        if (affiliationCode == null) affiliationCode = "unknown";

        System.out.println("affiliationCode: " + affiliationCode);

        sessionAffiliation.put(session, affiliationCode);
        RefreshObserver observer = event -> sendRefreshMessage(session, event);

        if ("101".equals(affiliationCode)) {
            adminSubject.registerObserver(observer);
        } else {
            userSubject.registerObserver(observer);
        }
        sessionObserverMap.put(session, observer);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String affiliationCode = sessionAffiliation.remove(session);
        RefreshObserver observer = sessionObserverMap.remove(session);

        if (observer != null) {
            if ("101".equals(affiliationCode)) {
                adminSubject.unregisterObserver(observer);
            } else {
                userSubject.unregisterObserver(observer);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> json = parseJson(payload);

        String action = (String) json.get("action");
        List<String> pages = (List<String>) json.get("pages");
        String affiliationCode = sessionAffiliation.get(session);

        if ("refreshRequest".equals(action)) {
            RefreshEvent event = new RefreshEvent(pages);
            if ("101".equals(affiliationCode)) {
                System.out.println("affiliationCode is 101: notifying userSubject");
                userSubject.notifyObservers(event);
            } else {
                System.out.println("affiliationCode is NOT 101: notifying adminSubject");
                adminSubject.notifyObservers(event);
            }
        }
    }

    private void sendRefreshMessage(WebSocketSession session, RefreshEvent event) {
        try {
            List<String> pages = event.getPagesToRefresh();

            if (pages.contains("storeManagement")) {
                session.sendMessage(new TextMessage(createJsonMessage("refresh", List.of("storeManagement"))));
            }
            if (pages.contains("requestList")) {
                session.sendMessage(new TextMessage(createJsonMessage("refresh", List.of("requestList"))));
            }
            if (pages.contains("itemList")) {
                session.sendMessage(new TextMessage(createJsonMessage("refresh", List.of("itemList"))));
            }
            if (pages.contains("affiliationRequestList")) {
                session.sendMessage(new TextMessage(createJsonMessage("refresh", List.of("affiliationRequestList"))));
            }
        } catch (Exception e) {
            // log error
        }
    }

    private Map<String, Object> parseJson(String jsonStr) throws Exception {
        return objectMapper.readValue(jsonStr, Map.class);
    }

    private String createJsonMessage(String command, List<String> pages) throws Exception {
        Map<String, Object> msg = new HashMap<>();
        msg.put("command", command);
        msg.put("pages", pages);
        return objectMapper.writeValueAsString(msg);
    }

    public void notifyAdmin(List<String> pages) {
        RefreshEvent event = new RefreshEvent(pages);
        adminSubject.notifyObservers(event);
    }

    public void notifyUser(List<String> pages) {
        RefreshEvent event = new RefreshEvent(pages);
        userSubject.notifyObservers(event);
    }

    private String extractAffiliationCode(URI uri) {
        if (uri == null) return null;

        String query = uri.getQuery();
        if (query == null) return null;

        String[] params = query.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv.length == 2 && kv[0].equals("affiliation_code")) {
                return kv[1];
            }
        }
        return null;
    }
}
