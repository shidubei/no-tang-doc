package com.ntdoc.notangdoccore.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntdoc.notangdoccore.controller.LogController;
import com.ntdoc.notangdoccore.entity.Log;
import com.ntdoc.notangdoccore.entity.logenum.ActorType;
import com.ntdoc.notangdoccore.entity.logenum.OperationStatus;
import com.ntdoc.notangdoccore.entity.logenum.OperationType;
import com.ntdoc.notangdoccore.repository.LogRepository;
import com.ntdoc.notangdoccore.service.log.LogService;
import jdk.dynalink.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

// LogController的单元测试
@WebMvcTest(LogController.class)
class LogControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogService logService;

    private Log mockLog;
    private List<Log> mockLogs;

    @BeforeEach
    void setUp() {
        mockLog = new Log();
        mockLog.setId(1L);
        mockLog.setActorType(ActorType.USER);
        mockLog.setActorName("test_user");
        mockLog.setUserId(100L);
        mockLog.setOperationType(OperationType.UPLOAD_DOCUMENT);
        mockLog.setTargetName("test.pdf");
        mockLog.setOperationStatus(OperationStatus.SUCCESS);
        mockLog.setMessage("Upload Successful");
        mockLog.setTime(Instant.now());

        mockLogs = Arrays.asList(mockLog);
    }


    // 测试 GET /api/logs/list 找到
    @Test
    void testGetLogs_Found() throws Exception {}

    //测试 GET /api/logs/list 未找到
    @Test
    void testGetLogs_NotFound() throws Exception {}

    //
}
