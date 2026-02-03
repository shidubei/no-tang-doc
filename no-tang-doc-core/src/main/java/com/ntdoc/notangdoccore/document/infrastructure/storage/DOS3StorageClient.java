package com.ntdoc.notangdoccore.document.infrastructure.storage;

import com.ntdoc.notangdoccore.config.s3.SpacesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DOS3StorageClient implements StorageClient {
    private final S3Client s3;
    private final S3Presigner presigner;
    private final SpacesProperties props;

    @Override
    public String bucket(){
        return props.getBucket();
    }

    @Override
    public String buildObjectKey(String teamId,String userId,String documentId,String originalFilename) {
        String safeName = sanitizeFilename(originalFilename);
        String prefix = trimSlashes(props.getKeyPrefix());

        /* 统一Key结构为
        * prefix/team/{teamId}/user/{userId}/doc/{documentId}/{fileName}
        * teamId 可以为空
        * */
        String teamPart = (teamId == null || teamId.isBlank())?"personal":"team-"+teamId;
        return String.format("%s/%s/user-%s/doc-%s/%s",
                prefix,teamPart,safe(userId),safe(documentId),safeName);
    }

    @Override
    public byte[] getObjectBytes(String objectKey){
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket())
                .key(objectKey)
                .build();

        ResponseInputStream<GetObjectResponse> in = s3.getObject(req);

        try(in){
            return in.readAllBytes();
        }catch(Exception e){
            throw new RuntimeException("Failed to read object:" + objectKey,e);
        }
    }

    @Override
    public PutResult putObject(String objectKey, byte[] bytes, String contentType){
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket())
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectResponse resp = s3.putObject(req, RequestBody.fromBytes(bytes));
        return new PutResult(resp.eTag());
    }

    @Override
    public InputStream getObjectStream(String objectKey){
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket())
                .key(objectKey)
                .build();

        return s3.getObject(req);
    }

    @Override
    public URL generatePresignedGetURL(String objectKey){
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket())
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(props.getPresignMinutes()))
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(presignReq).url();
    }

    @Override
    public void deleteObject(String objectKey){
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket())
                .key(objectKey)
                .build();
        s3.deleteObject(req);
    }


    private static String sanitizeFilename(String filename) {
        if(filename == null || filename.isBlank()) return "file";

        String base = filename.replace("\\","/");
        base = base.substring(base.lastIndexOf('/')+1);

        base = base.replaceAll("[^a-zA-Z0-9._-]","_");

        return base.toLowerCase(Locale.ROOT);
    }

    private static String trimSlashes(String s) {
        if (s==null || s.isBlank()) return "";
        String t = s;
        while(t.startsWith("/")) t=t.substring(1);
        while(t.endsWith("/")) t=t.substring(0, t.length()-1);
        return t.isBlank() ? "" : t;
    }

    private static String safe(String s){
        return (s==null || s.isBlank())? "unknown" : s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
