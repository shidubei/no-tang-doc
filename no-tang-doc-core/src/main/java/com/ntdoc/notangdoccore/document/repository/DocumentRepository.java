package com.ntdoc.notangdoccore.document.repository;

import com.ntdoc.notangdoccore.document.domain.model.Document;
import com.ntdoc.notangdoccore.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // 根据用户来查找文档
    @Query("SELECT d FROM Document d WHERE d.uploadedBy = :user ORDER BY d.createdAt DESC")
    List<Document> findByUploadedByOrderByCreatedAtDesc(@Param("user") User uploadedBy);

    //
}
