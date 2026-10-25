-- ====================================================================
-- CampusConnect Database Schema
-- Compatible with MySQL 8.0+
-- ====================================================================

CREATE DATABASE IF NOT EXISTS campusconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campusconnect;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'SENIOR', 'ALUMNI', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    department VARCHAR(100) NOT NULL,
    academic_year INT NOT NULL DEFAULT 1,
    career_goals TEXT,
    project_interests TEXT,
    club_memberships TEXT,
    bio TEXT,
    mentor_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    completed_sessions_count INT NOT NULL DEFAULT 0,
    credit_balance INT NOT NULL DEFAULT 50,
    status ENUM('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_role (role),
    INDEX idx_user_dept (department),
    INDEX idx_user_status (status)
) ENGINE=InnoDB;

-- 2. Skills Catalogue Table
CREATE TABLE IF NOT EXISTS skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_skill_category (category)
) ENGINE=InnoDB;

-- 3. User Skills (Offered to teach vs Wanted to learn)
CREATE TABLE IF NOT EXISTS user_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    skill_type ENUM('TEACH', 'LEARN') NOT NULL,
    proficiency_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_skill_type (user_id, skill_id, skill_type),
    CONSTRAINT fk_userskills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_userskills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    INDEX idx_userskills_type (skill_type)
) ENGINE=InnoDB;

-- 4. Mentor Availability Slots
CREATE TABLE IF NOT EXISTS mentor_availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    day_of_week VARCHAR(15) NOT NULL,
    specific_date DATE NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_availability_mentor FOREIGN KEY (mentor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_avail_mentor (mentor_id, is_active)
) ENGINE=InnoDB;

-- 5. Mentorship Sessions
CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    learner_id INT NOT NULL,
    skill_id INT NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    credit_cost INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_mentor FOREIGN KEY (mentor_id) REFERENCES users(id),
    CONSTRAINT fk_session_learner FOREIGN KEY (learner_id) REFERENCES users(id),
    CONSTRAINT fk_session_skill FOREIGN KEY (skill_id) REFERENCES skills(id),
    INDEX idx_session_date_time (session_date, start_time, end_time),
    INDEX idx_session_status (status)
) ENGINE=InnoDB;

-- 6. Skill Credit Ledger Transactions
CREATE TABLE IF NOT EXISTS skill_credit_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount INT NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    related_entity_type VARCHAR(50) NULL,
    related_entity_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_credit_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_credit_user (user_id, created_at)
) ENGINE=InnoDB;

-- 7. Session Feedback
CREATE TABLE IF NOT EXISTS session_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL UNIQUE,
    mentor_id INT NOT NULL,
    learner_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedback_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_mentor FOREIGN KEY (mentor_id) REFERENCES users(id),
    CONSTRAINT fk_feedback_learner FOREIGN KEY (learner_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- 8. Endorsements
CREATE TABLE IF NOT EXISTS endorsements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL UNIQUE,
    mentor_id INT NOT NULL,
    learner_id INT NOT NULL,
    skill_id INT NOT NULL,
    comment VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_endorsement_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_endorsement_mentor FOREIGN KEY (mentor_id) REFERENCES users(id),
    CONSTRAINT fk_endorsement_learner FOREIGN KEY (learner_id) REFERENCES users(id),
    CONSTRAINT fk_endorsement_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
) ENGINE=InnoDB;

-- 9. Doubt Board Questions
CREATE TABLE IF NOT EXISTS doubt_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    author_id INT NOT NULL,
    skill_id INT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    accepted_answer_id INT NULL,
    status ENUM('OPEN', 'RESOLVED', 'CLOSED', 'REPORTED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_doubt_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_doubt_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE SET NULL,
    INDEX idx_doubt_category (category),
    INDEX idx_doubt_status (status)
) ENGINE=InnoDB;

-- 10. Doubt Answers
CREATE TABLE IF NOT EXISTS doubt_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    author_id INT NOT NULL,
    answer_text TEXT NOT NULL,
    is_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES doubt_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answer_author FOREIGN KEY (author_id) REFERENCES users(id),
    INDEX idx_answer_question (question_id)
) ENGINE=InnoDB;

-- 11. Resource Library
CREATE TABLE IF NOT EXISTS resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uploader_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('Notes', 'Interview Experience', 'Question Paper', 'Project Report', 'Career Roadmap', 'Internship/Referral Posting') NOT NULL,
    tags VARCHAR(255),
    stored_filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    download_count INT NOT NULL DEFAULT 0,
    status ENUM('APPROVED', 'PENDING', 'REPORTED', 'REMOVED') NOT NULL DEFAULT 'APPROVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resource_uploader FOREIGN KEY (uploader_id) REFERENCES users(id),
    INDEX idx_resource_cat (category),
    INDEX idx_resource_status (status)
) ENGINE=InnoDB;

-- 12. Project Collaboration Marketplace
CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creator_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    required_skills VARCHAR(255) NOT NULL,
    available_positions INT NOT NULL DEFAULT 1,
    filled_positions INT NOT NULL DEFAULT 0,
    status ENUM('OPEN', 'FILLED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_creator FOREIGN KEY (creator_id) REFERENCES users(id),
    INDEX idx_project_status (status)
) ENGINE=InnoDB;

-- 13. Project Applications
CREATE TABLE IF NOT EXISTS project_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    applicant_id INT NOT NULL,
    pitch TEXT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_project_applicant (project_id, applicant_id),
    CONSTRAINT fk_app_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_user FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 14. Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    link VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user_read (user_id, is_read)
) ENGINE=InnoDB;

-- 15. Content Reports & Moderation
CREATE TABLE IF NOT EXISTS content_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reporter_id INT NOT NULL,
    content_type ENUM('QUESTION', 'ANSWER', 'RESOURCE', 'PROJECT', 'USER') NOT NULL,
    content_id INT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    INDEX idx_report_status (status)
) ENGINE=InnoDB;

-- 16. Admin Audit Logs
CREATE TABLE IF NOT EXISTS admin_audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id INT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_admin FOREIGN KEY (admin_id) REFERENCES users(id),
    INDEX idx_audit_admin (admin_id, created_at)
) ENGINE=InnoDB;
