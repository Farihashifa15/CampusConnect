package com.campusconnect;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Resource;
import com.campusconnect.model.User;
import com.campusconnect.service.ResourceService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceWorkflowTest {

    private ResourceService resourceService;
    private UserService userService;
    private User uploader;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        resourceService = new ResourceService();
        userService = new UserService();
        uploader = userService.registerUser("REG-UP1", "uploader@university.edu", "pass123", "Prof Smith", User.Role.SENIOR, "CS", 4);
    }

    @Test
    @DisplayName("TC09: Safe resource upload, search by keyword, and category filter")
    public void testResourceUploadAndSearch() throws SQLException, ValidationException {
        byte[] dummyContent = "Test resource content".getBytes();

        Resource r1 = resourceService.uploadResource(
            uploader.getId(),
            "Distributed Systems Architecture Notes",
            "Covers Raft and Paxos consensus algorithms",
            Resource.Category.NOTES,
            "Distributed, Raft, Consensus",
            "consensus_notes.pdf",
            dummyContent.length,
            "application/pdf",
            new ByteArrayInputStream(dummyContent)
        );
        assertNotNull(r1);
        assertTrue(r1.getId() > 0);
        assertTrue(r1.getStoredFilename().startsWith("res_"));
        assertTrue(r1.getStoredFilename().endsWith(".pdf"));

        // Search by keyword "consensus"
        List<Resource> searchRes = resourceService.searchResources("consensus", null, 10, 0);
        assertEquals(1, searchRes.size());
        assertEquals("Distributed Systems Architecture Notes", searchRes.get(0).getTitle());

        // Filter by category Notes
        List<Resource> catRes = resourceService.searchResources(null, "Notes", 10, 0);
        assertEquals(1, catRes.size());

        // Filter by different category Question Paper
        List<Resource> emptyCat = resourceService.searchResources(null, "Question Paper", 10, 0);
        assertEquals(0, emptyCat.size());
    }

    @Test
    @DisplayName("Security: Unsafe file extensions (exe, sh, js) must be rejected")
    public void testFileExtensionSecurity() {
        byte[] dummyContent = "malicious payload".getBytes();

        ValidationException ex = assertThrows(ValidationException.class, () ->
            resourceService.uploadResource(
                uploader.getId(),
                "Malicious Script",
                "Try executing",
                Resource.Category.NOTES,
                "hack",
                "virus.exe",
                dummyContent.length,
                "application/x-msdownload",
                new ByteArrayInputStream(dummyContent)
            )
        );
        assertTrue(ex.getMessage().contains("is not permitted"));
    }
}