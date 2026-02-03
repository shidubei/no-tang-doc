package com.ntdoc.notangdoccore.document.infrastructure.messaging;


import com.ntdoc.notangdoccore.config.kafka.TopicProperties;
import com.ntdoc.notangdoccore.document.domain.event.DocumentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentEventPublisher {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final TopicProperties topicProperties;

    public void publish(String documentId, DocumentEvent documentEvent) {
        kafkaTemplate.send(topicProperties.getDocEvents(),documentId,documentEvent);
    }
}
