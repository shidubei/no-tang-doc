package com.ntdoc.notangdoccore.document.infrastructure.storage;

import java.io.InputStream;
import java.net.URL;

public interface StorageClient {
    /*返回默认bucket名称*/
    String bucket();

    /*生成统一的S3对象路径objectKey
    * {prefix}/{teamPart}/user-{userId}/doc-{documentId}/{fileName}*/
    String buildObjectKey(String teamId,String userId, String documentId, String originalFilename);

    /*将S3对象一次性读取为byte[]并返回*/
    byte[] getObjectBytes(String objectKey);

    /*上传字节数组到S3,返回一个PutResult*/
    PutResult putObject(String objectKey, byte[] bytes, String contentType);

    /*返回一个输入流,用于流式读取对象内容*/
    InputStream getObjectStream(String objectKey);

    /*生成一个"短期有效"的下载URL,前端可以通过URL直接从Spaces下载对象*/
    URL generatePresignedGetURL(String objectKey);

    /*删除S3上的对象*/
    void deleteObject(String objectKey);

    record PutResult(String etag){}
}
