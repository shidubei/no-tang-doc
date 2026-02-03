package com.ntdoc.notangdoccore.document.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

// Can not change when it created
@Value
@Builder
public class DocumentUploadEvent implements DocumentEvent {
    String eventId;
    String eventType;
    Instant occurredAt;
    String documentId;
    String actorUserId;
    int schemaVersion;

    String bucket;
    String objectKey;
    String etag;
    String filename;
    String contentType;
    long size;
}
