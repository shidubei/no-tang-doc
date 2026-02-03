package com.ntdoc.notangdoccore.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public class TopicProperties {
    private String docEvents;
    private String auditEvents;

    public String getAuditEvents() {
        return auditEvents;
    }

    public void setAuditEvents(String auditEvents) {
        this.auditEvents = auditEvents;
    }

    public String getDocEvents() {
        return docEvents;
    }

    public void setDocEvents(String docEvents) {
        this.docEvents = docEvents;
    }
}
