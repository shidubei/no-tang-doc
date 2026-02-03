package com.ntdoc.notangdoccore.document.api.dto;



public record UploadResponse(
        String documentId,
        String bucket,
        String objectKey
){}
