package com.ntdoc.notangdoccore.document.domain.event;

import java.time.Instant;

// 文档事件统一接口
public interface DocumentEvent {
    String getEventId();
    String getEventType();
    Instant getOccurredAt();
    String getDocumentId();
    String getActorUserId();
    int getSchemaVersion();
}
