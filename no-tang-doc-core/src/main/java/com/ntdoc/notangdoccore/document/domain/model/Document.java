package com.ntdoc.notangdoccore.document.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {

    @Id
    private Long id;

    private String originalFilename;

    private String storedFilename;

    private Long fileSize;

    private String contentType;

    private String s3Bucket;

    private String s3Key;
}
