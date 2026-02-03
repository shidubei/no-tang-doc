package com.ntdoc.notangdoccore.document.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;


@Value
@Builder
public class DocumentDownloadEvent implements DocumentEvent{
    String eventId;
    String eventType;
    Instant occurredAt;
    String documentId;
    String actorUserId;
    int schemaVersion;

    String bucket;
    String objectKey;
}
