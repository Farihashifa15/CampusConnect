package com.campusconnect;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Project;
import com.campusconnect.model.ProjectApplication;
import com.campusconnect.model.User;
import com.campusconnect.service.ProjectService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectCollaborationWorkflowTest {

    private ProjectService projectService;
    private UserService userService;

    private User lead;
    private User applicant1;
    private User applicant2;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        projectService = new ProjectService();
        userService = new UserService();

        lead = userService.registerUser("REG-PL", "lead@university.edu", "pass123", "Project Lead", User.Role.SENIOR, "CS", 4);
        applicant1 = userService.registerUser("REG-AP1", "app1@university.edu", "pass123", "Applicant One", User.Role.STUDENT, "IT", 2);
        applicant2 = userService.registerUser("REG-AP2", "app2@university.edu", "pass123", "Applicant Two", User.Role.STUDENT, "CS", 1);
    }

    @Test
    @DisplayName("Projects: Create project listing, apply with pitch, and enforce position limits")
    public void testProjectCollaborationLifecycle() throws SQLException, ValidationException {
        // Create project with exactly 1 available position
        Project project = projectService.createProject(
            lead.getId(),
            "Campus Autonomous Rover",
            "Designing an autonomous robot using ROS and OpenCV",
            "Robotics & Embedded Systems",
            "Python, ROS, Computer Vision",
            1
        );
        assertNotNull(project);
        assertEquals(1, project.getAvailablePositions());
        assertEquals(0, project.getFilledPositions());
        assertEquals(Project.Status.OPEN, project.getStatus());

        // 1. Lead cannot apply to own project
        ValidationException selfApp = assertThrows(ValidationException.class, () ->
            projectService.applyToProject(project.getId(), lead.getId(), "I am the creator")
        );
        assertTrue(selfApp.getMessage().contains("cannot apply to your own project"));

        // 2. Applicant 1 applies
        ProjectApplication app1 = projectService.applyToProject(project.getId(), applicant1.getId(), "Experienced in ROS and Python");
        assertNotNull(app1);
        assertEquals(ProjectApplication.Status.PENDING, app1.getStatus());

        // 3. Duplicate application attempt by same student must be prevented
        ValidationException dupApp = assertThrows(ValidationException.class, () ->
            projectService.applyToProject(project.getId(), applicant1.getId(), "Applying again")
        );
        assertTrue(dupApp.getMessage().contains("already applied"));

        // 4. Lead accepts applicant 1 -> project fills available position and closes
        boolean accepted = projectService.respondToApplication(lead.getId(), app1.getId(), true);
        assertTrue(accepted);

        Project updatedProject = projectService.getProject(project.getId());
        assertEquals(1, updatedProject.getFilledPositions());
        assertEquals(Project.Status.FILLED, updatedProject.getStatus());

        // 5. Applicant 2 applying to full project must be rejected
        ValidationException fullEx = assertThrows(ValidationException.class, () ->
            projectService.applyToProject(project.getId(), applicant2.getId(), "Please let me in")
        );
        assertTrue(fullEx.getMessage().contains("no remaining open positions"));
    }
}