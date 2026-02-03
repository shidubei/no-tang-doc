package com.ntdoc.notangdoccore.document.application;

import com.ntdoc.notangdoccore.document.api.dto.DownloadUrlResponse;
import com.ntdoc.notangdoccore.document.api.dto.UploadResponse;
import com.ntdoc.notangdoccore.document.domain.event.DocumentUploadEvent;
import com.ntdoc.notangdoccore.document.infrastructure.messaging.DocumentEventPublisher;
import com.ntdoc.notangdoccore.document.infrastructure.persistence.DocumentEntity;
import com.ntdoc.notangdoccore.document.infrastructure.persistence.DocumentRepository;
import com.ntdoc.notangdoccore.document.infrastructure.storage.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl {
    /*TODO:
    * 1.基本的文件上传和下载逻辑
    * 2.事务完成后的消息发送,利用Kafka进行*/
    private final StorageClient storageClient;
    private final DocumentRepository documentRepository;
    private final DocumentEventPublisher eventPublisher;

    @Transactional
    public UploadResponse uploadDocument(MultipartFile file, String actorUserId, String teamId) {
        try{
            String documentId = UUID.randomUUID().toString();

            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("file");

            String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
            byte[] bytes = file.getBytes();

            String objectKey = storageClient.buildObjectKey(teamId,actorUserId,documentId,contentType);
            StorageClient.PutResult put = storageClient.putObject(objectKey,bytes,contentType);

            //TODO:
            //编写合适的DocumentEntity
            DocumentEntity entity = DocumentEntity.builder()
                    .s3Bucket(storageClient.bucket())
                    .originalFilename(fileName)
                    .build();

            //发布DocumentUploadEvent
            DocumentUploadEvent event = DocumentUploadEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("DocumentUpload")
                    .occurredAt(Instant.now())
                    .schemaVersion(1)
                    .documentId(documentId)
                    .actorUserId(actorUserId)
                    .bucket(storageClient.bucket())
                    .objectKey(objectKey)
                    .etag(put.etag())
                    .filename(fileName)
                    .contentType(contentType)
                    .size(bytes.length)
                    .build();

            eventPublisher.publish(documentId,event);

            return new UploadResponse(documentId,storageClient.bucket(),objectKey);
        }catch(IOException e){
            throw new RuntimeException("Upload Document Failed",e);
        }
    }

    @Transactional(readOnly = true)
    public DownloadUrlResponse getDownloadUrl(String documentId,String actorUserId,String clientIp,String userAgent){

        // 权限校验,只允许owner下载

    }
}
