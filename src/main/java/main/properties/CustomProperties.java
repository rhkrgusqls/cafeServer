package main.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

// application.properties에 있는 custom.* 값을 모두 바인딩
@Configuration
@ConfigurationProperties(prefix = "custom")
@Component
public class CustomProperties {

    private String affiliationCode;

    public String getAffiliationCode() {
        return affiliationCode;
    }

    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }
}
