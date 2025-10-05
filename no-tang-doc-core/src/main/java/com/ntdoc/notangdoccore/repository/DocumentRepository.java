package com.ntdoc.notangdoccore.repository;

import com.ntdoc.notangdoccore.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}