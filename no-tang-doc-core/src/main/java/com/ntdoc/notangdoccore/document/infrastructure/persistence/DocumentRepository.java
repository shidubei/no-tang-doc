package com.ntdoc.notangdoccore.document.infrastructure.persistence;

import com.ntdoc.notangdoccore.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    // 根据用户来查找文档
    @Query("SELECT d FROM DocumentEntity d WHERE d.uploadedBy = :user ORDER BY d.createdAt DESC")
    List<DocumentEntity> findByUploadedByOrderByCreatedAtDesc(@Param("user") User uploadedBy);

    //
}
