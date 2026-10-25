-- ====================================================================
-- CampusConnect Seed Data for Local Development and Testing
-- Password for all accounts: password123
-- ====================================================================

USE campusconnect;

-- Clear previous seed data if exists (in reverse foreign key order)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE admin_audit_logs;
TRUNCATE TABLE content_reports;
TRUNCATE TABLE notifications;
TRUNCATE TABLE project_applications;
TRUNCATE TABLE projects;
TRUNCATE TABLE resources;
TRUNCATE TABLE doubt_answers;
TRUNCATE TABLE doubt_questions;
TRUNCATE TABLE endorsements;
TRUNCATE TABLE session_feedback;
TRUNCATE TABLE skill_credit_transactions;
TRUNCATE TABLE sessions;
TRUNCATE TABLE mentor_availability;
TRUNCATE TABLE user_skills;
TRUNCATE TABLE skills;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insert Users
-- Common Hash for "password123": $2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6
INSERT INTO users (id, register_number, email, password_hash, full_name, role, department, academic_year, career_goals, project_interests, club_memberships, bio, mentor_rating, completed_sessions_count, credit_balance, status)
VALUES
(1, 'REG-ADM01', 'admin@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Dr. Alan Turing', 'ADMIN', 'Computer Science & Engineering', 5, 'Advancing university research and mentoring ecosystem.', 'Distributed Systems, High-Performance Computing', 'ACM Faculty Advisor', 'Department Chair & CampusConnect Lead Administrator.', 5.00, 50, 500, 'ACTIVE'),
(2, 'REG-SNR01', 'senior.sarah@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Sarah Jenkins', 'SENIOR', 'Computer Science & Engineering', 4, 'Aspiring Cloud Infrastructure Architect at top tech firm.', 'Distributed Microservices, Kubernetes orchestrations', 'Google Developer Student Clubs Lead, Coding Club', 'Final year CSE senior passionate about backend engineering, Java, and scalable system design.', 4.80, 12, 120, 'ACTIVE'),
(3, 'REG-ALM01', 'alumni.marcus@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Marcus Vance', 'ALUMNI', 'Software Engineering', 4, 'Senior Software Engineer at AWS. Helping students break into tech.', 'Large scale data streaming, Kafka, Golang', 'Alumni Mentorship Network, IEEE Senior Member', 'Class of 2022 Alumni. Currently working on distributed databases and cloud services.', 4.90, 25, 250, 'ACTIVE'),
(4, 'REG-SNR02', 'senior.priya@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Priya Sharma', 'SENIOR', 'Data Science & AI', 3, 'Machine Learning Researcher in NLP and Generative AI.', 'Computer Vision, Transformer architectures', 'AI & Robotics Club President', 'Third year student mentoring in Python, ML algorithms, and competitive programming.', 4.60, 8, 90, 'ACTIVE'),
(5, 'REG-STU01', 'student.alex@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Alex Rivera', 'STUDENT', 'Information Technology', 2, 'Full Stack Web Developer and open source enthusiast.', 'Web platforms, collaborative editing tools', 'Web Development Society', 'Sophomore eager to master Java enterprise backend, Spring, and database optimization.', 0.00, 0, 50, 'ACTIVE'),
(6, 'REG-STU02', 'student.elena@university.edu', '$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6', 'Elena Rostova', 'STUDENT', 'Computer Science & Engineering', 1, 'Software Engineer focusing on algorithm design.', 'Graph algorithms, game development', 'Competitive Programming Chapter', 'Freshman eager to learn Data Structures, Python, and prepare for internships.', 0.00, 0, 50, 'ACTIVE');

-- 2. Insert Skills Catalogue
INSERT INTO skills (id, name, category, description)
VALUES
(1, 'Java Programming', 'Programming Languages', 'Core Java, OOP principles, Concurrency, and Stream API'),
(2, 'Spring Boot & Microservices', 'Backend Development', 'RESTful APIs, Spring Data, and microservice architectures'),
(3, 'MySQL & Database Design', 'Databases', 'Relational normalization, indexing, query optimization, and transactions'),
(4, 'Python for Data Science', 'Data Science', 'NumPy, Pandas, Matplotlib, and exploratory data analysis'),
(5, 'Data Structures & Algorithms', 'Computer Science Core', 'Trees, Graphs, Dynamic Programming, and algorithm complexity'),
(6, 'Machine Learning & Deep Learning', 'Artificial Intelligence', 'Scikit-Learn, PyTorch, model evaluation, and Neural Networks'),
(7, 'System Design & Architecture', 'Software Engineering', 'Scalability, caching, load balancing, and high availability systems'),
(8, 'Cloud Computing (AWS/GCP)', 'Cloud & DevOps', 'EC2, S3, Docker, Kubernetes, and CI/CD pipelines'),
(9, 'Web Development (HTML/CSS/JS)', 'Frontend Development', 'Modern responsive UI design, ES6, and frontend integration'),
(10, 'Technical Interview Preparation', 'Career Guidance', 'Mock interviews, resume critique, and behavioral preparation');

-- 3. Insert User Skills (TEACH vs LEARN)
INSERT INTO user_skills (user_id, skill_id, skill_type, proficiency_level)
VALUES
-- Sarah Jenkins (TEACH)
(2, 1, 'TEACH', 'ADVANCED'),
(2, 2, 'TEACH', 'ADVANCED'),
(2, 7, 'TEACH', 'INTERMEDIATE'),
-- Sarah Jenkins (LEARN)
(2, 8, 'LEARN', 'BEGINNER'),
-- Marcus Vance (TEACH)
(3, 7, 'TEACH', 'ADVANCED'),
(3, 8, 'TEACH', 'ADVANCED'),
(3, 10, 'TEACH', 'ADVANCED'),
-- Priya Sharma (TEACH)
(4, 4, 'TEACH', 'ADVANCED'),
(4, 5, 'TEACH', 'ADVANCED'),
(4, 6, 'TEACH', 'INTERMEDIATE'),
-- Alex Rivera (LEARN)
(5, 1, 'LEARN', 'INTERMEDIATE'),
(5, 2, 'LEARN', 'BEGINNER'),
(5, 3, 'LEARN', 'INTERMEDIATE'),
-- Elena Rostova (LEARN)
(6, 4, 'LEARN', 'BEGINNER'),
(6, 5, 'LEARN', 'BEGINNER');

-- 4. Insert Mentor Availability
INSERT INTO mentor_availability (mentor_id, day_of_week, specific_date, start_time, end_time, is_active)
VALUES
(2, 'MONDAY', NULL, '10:00:00', '12:00:00', TRUE),
(2, 'WEDNESDAY', NULL, '14:00:00', '16:00:00', TRUE),
(2, 'FRIDAY', NULL, '15:00:00', '17:00:00', TRUE),
(3, 'SATURDAY', NULL, '10:00:00', '13:00:00', TRUE),
(3, 'SUNDAY', NULL, '11:00:00', '14:00:00', TRUE),
(4, 'TUESDAY', NULL, '16:00:00', '18:00:00', TRUE),
(4, 'THURSDAY', NULL, '16:00:00', '18:00:00', TRUE);

-- 5. Insert Completed & Pending Sessions
INSERT INTO sessions (id, mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost)
VALUES
(1, 2, 5, 1, DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), '10:00:00', '11:00:00', 'COMPLETED', 'Discussed Java Collections framework and concurrency fundamentals.', 10),
(2, 3, 5, 10, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), '10:00:00', '11:00:00', 'COMPLETED', 'Resume review and mock technical behavioral questions.', 10),
(3, 4, 6, 5, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), '16:00:00', '17:00:00', 'CONFIRMED', 'Binary Search and Recursion review.', 10),
(4, 2, 6, 1, DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), '10:00:00', '11:00:00', 'PENDING', 'Introduction to Java OOP and classes.', 10);

-- 6. Insert Skill Credit Ledger Transactions
INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id)
VALUES
(5, 50, 'INITIAL_GRANT', 'Welcome grant upon university account registration', 'USER', 5),
(6, 50, 'INITIAL_GRANT', 'Welcome grant upon university account registration', 'USER', 6),
(5, -10, 'SESSION_BOOKING', 'Mentorship session booking fee for Java Programming', 'SESSION', 1),
(2, 10, 'SESSION_COMPLETION', 'Mentorship reward for completed session with Alex Rivera', 'SESSION', 1),
(5, -10, 'SESSION_BOOKING', 'Mentorship session booking fee for Technical Interview Preparation', 'SESSION', 2),
(3, 10, 'SESSION_COMPLETION', 'Mentorship reward for completed session with Alex Rivera', 'SESSION', 2),
(6, -10, 'SESSION_BOOKING', 'Mentorship session booking fee for Data Structures & Algorithms', 'SESSION', 3);

-- 7. Insert Session Feedback
INSERT INTO session_feedback (session_id, mentor_id, learner_id, rating, feedback_text)
VALUES
(1, 2, 5, 5, 'Sarah was fantastic! She explained Java memory model and multithreading with crystal clarity. Highly recommended!'),
(2, 3, 5, 5, 'Marcus provided invaluable insights into AWS software engineer interviews and gave practical tips on structuring answers.');

-- 8. Insert Endorsements
INSERT INTO endorsements (session_id, mentor_id, learner_id, skill_id, comment)
VALUES
(1, 2, 5, 1, 'Endorsed for exceptional mastery of Java OOP and Concurrency fundamentals.'),
(2, 3, 5, 10, 'Endorsed for expert Technical Interview coaching and resume refinement.');

-- 9. Insert Doubt Questions
INSERT INTO doubt_questions (id, author_id, skill_id, category, title, description, accepted_answer_id, status)
VALUES
(1, 5, 1, 'Backend Development', 'How does ConcurrentHashMap achieve thread safety without locking the whole table in Java 8+?', 'I understand HashMap is not thread-safe, and Hashtable locks the entire map. How does Java 8+ ConcurrentHashMap implement bucket-level synchronization with CAS?', 1, 'RESOLVED'),
(2, 6, 5, 'Computer Science Core', 'Difference between BFS and DFS for finding the shortest path in unweighted vs weighted graphs?', 'Can someone clarify when BFS guarantees the shortest path, and when we should switch to Dijkstra or Bellman-Ford?', NULL, 'OPEN'),
(3, 5, 3, 'Databases', 'When should composite indexes be chosen over multiple single-column indexes in MySQL?', 'In an e-commerce database with queries filtering on both user_id and created_at, should I create two separate indexes or one composite index?', NULL, 'OPEN');

-- 10. Insert Doubt Answers
INSERT INTO doubt_answers (id, question_id, author_id, answer_text, is_accepted)
VALUES
(1, 1, 2, 'In Java 8+, ConcurrentHashMap dropped Segment locks in favor of CAS (Compare-And-Swap) for empty bucket insertion and synchronized blocks on individual head nodes for occupied buckets. This ensures fine-grained concurrency with minimal thread contention.', TRUE),
(2, 2, 4, 'BFS guarantees the shortest path in unweighted graphs because it explores vertices layer by layer in increasing order of edge count. For weighted graphs with non-negative weights, BFS fails because edge counts do not equal distance; use Dijkstras algorithm instead.', FALSE);

-- 11. Insert Resources
INSERT INTO resources (id, uploader_id, title, description, category, tags, stored_filename, original_filename, file_size, mime_type, download_count, status)
VALUES
(1, 2, 'Java Concurrency & Multithreading Comprehensive Cheatsheet', 'A deep dive into Java memory models, thread pools, locks, and atomic variables.', 'Notes', 'Java, Concurrency, JVM, MultiThreading', 'res_java_concurrency.pdf', 'Java_Concurrency_Guide.pdf', 2048500, 'application/pdf', 38, 'APPROVED'),
(2, 3, 'AWS SDE-II Interview Experience & System Design Breakdown', 'Detailed breakdown of system design rounds, distributed cache questions, and leadership principles.', 'Interview Experience', 'AWS, SystemDesign, Interview, Cloud', 'res_aws_interview.pdf', 'AWS_SDE2_Interview_Exp.pdf', 1450200, 'application/pdf', 84, 'APPROVED'),
(3, 4, 'Data Structures & Algorithms Semester Midterm Solved Papers', 'Full solutions with complexity analyses for trees, graphs, sorting, and dynamic programming.', 'Question Paper', 'DSA, Algorithms, Exams, Solutions', 'res_dsa_exam_solutions.pdf', 'DSA_Midterm_Solved_2024.pdf', 3120000, 'application/pdf', 52, 'APPROVED');

-- 12. Insert Projects
INSERT INTO projects (id, creator_id, title, description, category, required_skills, available_positions, filled_positions, status)
VALUES
(1, 2, 'CampusConnect Mobile Companion App', 'Developing a lightweight cross-platform Flutter/React-Native companion app for push notifications and instant chat.', 'Mobile & Web', 'React Native, Flutter, REST API', 2, 0, 'OPEN'),
(2, 4, 'Automated Medical Image Classifier using Vision Transformers', 'An AI research project analyzing chest X-rays using hybrid ViT architectures. Looking for collaborators.', 'Artificial Intelligence', 'PyTorch, Python, Computer Vision, Transformers', 2, 1, 'OPEN'),
(3, 5, 'Peer Study Group Organizer & Resource Indexer', 'A collaborative desktop tool for organizing course notes and generating flashcards.', 'Productivity Tools', 'Java, JavaFX, SQLite', 1, 1, 'FILLED');

-- 13. Insert Project Applications
INSERT INTO project_applications (id, project_id, applicant_id, pitch, status)
VALUES
(1, 1, 5, 'Hi Sarah! I have hands-on experience building mobile apps and consuming REST APIs in React Native. Id love to contribute!', 'PENDING'),
(2, 2, 6, 'I have completed coursework in Python and Linear Algebra, and want to learn PyTorch through this hands-on research project.', 'ACCEPTED');

-- 14. Insert Notifications
INSERT INTO notifications (user_id, title, message, type, link, is_read)
VALUES
(5, 'Session Completed', 'Your mentorship session on Java Programming with Sarah Jenkins has been completed. Please submit your feedback and endorsement!', 'SESSION', '/sessions/view?id=1', FALSE),
(2, 'Skill Credits Earned', 'You earned 10 Skill Credits for successfully mentoring Alex Rivera in Java Programming.', 'CREDIT', '/credits', FALSE),
(6, 'Session Confirmed', 'Your mentorship session with Priya Sharma on Data Structures & Algorithms is confirmed for Thursday.', 'SESSION', '/sessions/view?id=3', TRUE),
(2, 'New Project Application', 'Alex Rivera applied to join your project: CampusConnect Mobile Companion App.', 'APPLICATION', '/projects/view?id=1', FALSE);

-- 15. Insert Content Reports
INSERT INTO content_reports (reporter_id, content_type, content_id, reason, status)
VALUES
(5, 'RESOURCE', 3, 'Formatting issue in section 4.2 of the solutions document, requesting clean re-upload.', 'PENDING');

-- 16. Insert Admin Audit Logs
INSERT INTO admin_audit_logs (admin_id, action, target_type, target_id, details, ip_address)
VALUES
(1, 'SYSTEM_INITIALIZATION', 'SYSTEM', NULL, 'Database schema and initial seed data initialized successfully.', '127.0.0.1'),
(1, 'USER_ROLE_ASSIGNED', 'USER', 2, 'Assigned SENIOR role to Sarah Jenkins with verified university credentials.', '127.0.0.1'),
(1, 'CREDIT_POLICY_UPDATE', 'POLICY', NULL, 'Initial session booking cost set to 10 credits; mentor completion reward set to 10 credits.', '127.0.0.1');