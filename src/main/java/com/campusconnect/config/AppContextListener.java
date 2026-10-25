
package com.campusconnect.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.log("CampusConnect: Application starting up...");

        try {
            DatabaseConfig.initialize("db.properties");
            ctx.log("CampusConnect: Database connection pool initialized.");

            runSqlResource("h2-init.sql", ctx);
            verifyRequiredTables(DatabaseConfig.getConnection());

            try (Connection conn = DatabaseConfig.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    boolean alreadySeeded;

                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery(
                                 "SELECT COUNT(*) FROM _seed_applied")) {
                        rs.next();
                        alreadySeeded = rs.getInt(1) > 0;
                    }

                    if (!alreadySeeded) {
                        ctx.log("CampusConnect: Loading initial seed data...");
                        seedDatabase(conn, ctx);

                        try (Statement st = conn.createStatement()) {
                            st.executeUpdate(
                                    "INSERT INTO _seed_applied (id) VALUES (1)");
                        }

                        conn.commit();
                        ctx.log("CampusConnect: Seed data loaded successfully.");
                    } else {
                        conn.rollback();
                        ctx.log("CampusConnect: Database already seeded.");
                    }
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            ctx.log("CampusConnect: Startup initialization completed.");
        } catch (Exception e) {
            ctx.log("CampusConnect: STARTUP INITIALIZATION FAILED", e);
            throw new IllegalStateException(
                    "CampusConnect database initialization failed: "
                            + e.getMessage(), e);
        }
    }

    private void runSqlResource(String resourceName, ServletContext ctx)
            throws Exception {

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(resourceName)) {

            if (input == null) {
                throw new IllegalStateException(
                        "SQL resource not found: " + resourceName);
            }

            String sql = new String(
                    input.readAllBytes(), StandardCharsets.UTF_8);

            // Remove full-line SQL comments before splitting statements.
            sql = Pattern.compile("(?m)^\\s*--.*(?:\\R|$)")
                    .matcher(sql)
                    .replaceAll("");

            try (Connection conn = DatabaseConfig.getConnection();
                 Statement statement = conn.createStatement()) {

                for (String part : sql.split(";")) {
                    String query = part.trim();

                    if (query.isEmpty()) {
                        continue;
                    }

                    try {
                        statement.execute(query);
                    } catch (Exception e) {
                        ctx.log("CampusConnect: Failed SQL statement:\n"
                                + query, e);
                        throw e;
                    }
                }
            }
        }

        ctx.log("CampusConnect: Schema initialization completed.");
    }

    private void verifyRequiredTables(Connection conn) throws Exception {
        try (Connection connection = conn;
             ResultSet tables = connection.getMetaData()
                     .getTables(null, null, null, new String[]{"TABLE"})) {

            java.util.Set<String> existing = new java.util.HashSet<>();

            while (tables.next()) {
                existing.add(tables.getString("TABLE_NAME")
                        .toUpperCase(java.util.Locale.ROOT));
            }

            String[] requiredTables = {
                    "USERS", "SKILLS", "USER_SKILLS",
                    "MENTOR_AVAILABILITY", "SESSIONS",
                    "SKILL_CREDIT_TRANSACTIONS", "SESSION_FEEDBACK",
                    "ENDORSEMENTS", "DOUBT_QUESTIONS", "DOUBT_ANSWERS",
                    "RESOURCES", "PROJECTS", "PROJECT_APPLICATIONS",
                    "NOTIFICATIONS", "CONTENT_REPORTS",
                    "ADMIN_AUDIT_LOGS", "_SEED_APPLIED"
            };

            for (String table : requiredTables) {
                if (!existing.contains(table)) {
                    throw new IllegalStateException(
                            "Required database table is missing: " + table);
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConfig.shutdown();
        sce.getServletContext()
                .log("CampusConnect: Database connection pool shut down.");
    }

    private void seedDatabase(Connection conn, ServletContext ctx)
            throws Exception {

        // BCrypt hash of "password123".
        final String hash =
                "$2a$10$Z78it7PYHgVO/VVo.ULLYOAOnOp7z5zAtmGnoywzCAIVsoYJk4sI6";

        String[][] users = {
                {"1","REG-ADM01","admin@university.edu","Dr. Alan Turing","ADMIN","Computer Science & Engineering","5","Advancing university research and mentoring ecosystem.","Distributed Systems, High-Performance Computing","ACM Faculty Advisor","Department Chair & CampusConnect Lead Administrator.","5.00","50","500"},
                {"2","REG-SNR01","senior.sarah@university.edu","Sarah Jenkins","SENIOR","Computer Science & Engineering","4","Aspiring Cloud Infrastructure Architect at top tech firm.","Distributed Microservices, Kubernetes orchestrations","Google Developer Student Clubs Lead, Coding Club","Final year CSE senior passionate about backend engineering, Java, and scalable system design.","4.80","12","120"},
                {"3","REG-ALM01","alumni.marcus@university.edu","Marcus Vance","ALUMNI","Software Engineering","4","Senior Software Engineer at AWS. Helping students break into tech.","Large scale data streaming, Kafka, Golang","Alumni Mentorship Network, IEEE Senior Member","Class of 2022 Alumni. Currently working on distributed databases and cloud services.","4.90","25","250"},
                {"4","REG-SNR02","senior.priya@university.edu","Priya Sharma","SENIOR","Data Science & AI","3","Machine Learning Researcher in NLP and Generative AI.","Computer Vision, Transformer architectures","AI & Robotics Club President","Third year student mentoring in Python, ML algorithms, and competitive programming.","4.60","8","90"},
                {"5","REG-STU01","student.alex@university.edu","Alex Rivera","STUDENT","Information Technology","2","Full Stack Web Developer and open source enthusiast.","Web platforms, collaborative editing tools","Web Development Society","Sophomore eager to master Java enterprise backend, Spring, and database optimization.","0.00","0","50"},
                {"6","REG-STU02","student.elena@university.edu","Elena Rostova","STUDENT","Computer Science & Engineering","1","Software Engineer focusing on algorithm design.","Graph algorithms, game development","Competitive Programming Chapter","Freshman eager to learn Data Structures, Python, and prepare for internships.","0.00","0","50"}
        };

        try (Statement st = conn.createStatement()) {
            for (String[] u : users) {
                st.executeUpdate(
                        "INSERT INTO users (id, register_number, email, password_hash, full_name, role, department, academic_year, career_goals, project_interests, club_memberships, bio, mentor_rating, completed_sessions_count, credit_balance, status) VALUES ("
                                + u[0] + ", '" + escape(u[1]) + "', '"
                                + escape(u[2]) + "', '" + hash + "', '"
                                + escape(u[3]) + "', '" + u[4] + "', '"
                                + escape(u[5]) + "', " + u[6] + ", '"
                                + escape(u[7]) + "', '" + escape(u[8])
                                + "', '" + escape(u[9]) + "', '"
                                + escape(u[10]) + "', " + u[11] + ", "
                                + u[12] + ", " + u[13] + ", 'ACTIVE')");
            }

            String[][] skills = {
                    {"1","Java Programming","Programming Languages","Core Java, OOP principles, Concurrency, and Stream API"},
                    {"2","Spring Boot & Microservices","Backend Development","RESTful APIs, Spring Data, and microservice architectures"},
                    {"3","MySQL & Database Design","Databases","Relational normalization, indexing, query optimization, and transactions"},
                    {"4","Python for Data Science","Data Science","NumPy, Pandas, Matplotlib, and exploratory data analysis"},
                    {"5","Data Structures & Algorithms","Computer Science Core","Trees, Graphs, Dynamic Programming, and algorithm complexity"},
                    {"6","Machine Learning & Deep Learning","Artificial Intelligence","Scikit-Learn, PyTorch, model evaluation, and Neural Networks"},
                    {"7","System Design & Architecture","Software Engineering","Scalability, caching, load balancing, and high availability systems"},
                    {"8","Cloud Computing (AWS/GCP)","Cloud & DevOps","EC2, S3, Docker, Kubernetes, and CI/CD pipelines"},
                    {"9","Web Development (HTML/CSS/JS)","Frontend Development","Modern responsive UI design, ES6, and frontend integration"},
                    {"10","Technical Interview Preparation","Career Guidance","Mock interviews, resume critique, and behavioral preparation"}
            };

            for (String[] s : skills) {
                st.executeUpdate(
                        "INSERT INTO skills (id, name, category, description) VALUES ("
                                + s[0] + ", '" + escape(s[1]) + "', '"
                                + escape(s[2]) + "', '" + escape(s[3]) + "')");
            }

            int[][] userSkills = {
                    {2,1,0,0},{2,2,0,0},{2,7,0,1},{2,8,2,2},
                    {3,7,0,0},{3,8,0,0},{3,10,0,0},
                    {4,4,0,0},{4,5,0,0},{4,6,2,1},
                    {5,1,2,1},{5,2,2,2},{5,3,2,1},
                    {6,4,2,2},{6,5,2,2}
            };

            String[] skillTypes = {"TEACH","TEACH","LEARN"};
            String[] profLevels = {"ADVANCED","INTERMEDIATE","BEGINNER"};

            for (int[] us : userSkills) {
                st.executeUpdate(
                        "INSERT INTO user_skills (user_id, skill_id, skill_type, proficiency_level) VALUES ("
                                + us[0] + ", " + us[1] + ", '"
                                + skillTypes[us[2]] + "', '"
                                + profLevels[us[3]] + "')");
            }

            String[] availability = {
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (2,'MONDAY','10:00:00','12:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (2,'WEDNESDAY','14:00:00','16:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (2,'FRIDAY','15:00:00','17:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (3,'SATURDAY','10:00:00','13:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (3,'SUNDAY','11:00:00','14:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (4,'TUESDAY','16:00:00','18:00:00',TRUE)",
                    "INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active) VALUES (4,'THURSDAY','16:00:00','18:00:00',TRUE)"
            };
            for (String q : availability) st.executeUpdate(q);

            String[] remaining = {
                    "INSERT INTO sessions (id, mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost) VALUES (1,2,5,1,DATEADD('DAY',-3,CURRENT_DATE),'10:00:00','11:00:00','COMPLETED','Discussed Java Collections framework and concurrency fundamentals.',10)",
                    "INSERT INTO sessions (id, mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost) VALUES (2,3,5,10,DATEADD('DAY',-1,CURRENT_DATE),'10:00:00','11:00:00','COMPLETED','Resume review and mock technical behavioral questions.',10)",
                    "INSERT INTO sessions (id, mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost) VALUES (3,4,6,5,DATEADD('DAY',2,CURRENT_DATE),'16:00:00','17:00:00','CONFIRMED','Binary Search and Recursion review.',10)",
                    "INSERT INTO sessions (id, mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost) VALUES (4,2,6,1,DATEADD('DAY',3,CURRENT_DATE),'10:00:00','11:00:00','PENDING','Introduction to Java OOP and classes.',10)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (5,50,'INITIAL_GRANT','Welcome grant upon university account registration','USER',5)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (6,50,'INITIAL_GRANT','Welcome grant upon university account registration','USER',6)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (5,-10,'SESSION_BOOKING','Mentorship session booking fee for Java Programming','SESSION',1)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (2,10,'SESSION_COMPLETION','Mentorship reward for completed session with Alex Rivera','SESSION',1)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (5,-10,'SESSION_BOOKING','Mentorship session booking fee for Technical Interview Preparation','SESSION',2)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (3,10,'SESSION_COMPLETION','Mentorship reward for completed session with Alex Rivera','SESSION',2)",
                    "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) VALUES (6,-10,'SESSION_BOOKING','Mentorship session booking fee for Data Structures & Algorithms','SESSION',3)",
                    "INSERT INTO session_feedback (session_id, mentor_id, learner_id, rating, feedback_text) VALUES (1,2,5,5,'Sarah was fantastic! She explained Java memory model and multithreading with crystal clarity. Highly recommended!')",
                    "INSERT INTO session_feedback (session_id, mentor_id, learner_id, rating, feedback_text) VALUES (2,3,5,5,'Marcus provided invaluable insights into AWS software engineer interviews and gave practical tips on structuring answers.')",
                    "INSERT INTO endorsements (session_id, mentor_id, learner_id, skill_id, comment) VALUES (1,2,5,1,'Endorsed for exceptional mastery of Java OOP and Concurrency fundamentals.')",
                    "INSERT INTO endorsements (session_id, mentor_id, learner_id, skill_id, comment) VALUES (2,3,5,10,'Endorsed for expert Technical Interview coaching and resume refinement.')",
                    "INSERT INTO doubt_questions (id,author_id,skill_id,category,title,description,accepted_answer_id,status) VALUES (1,5,1,'Backend Development','How does ConcurrentHashMap achieve thread safety without locking the whole table in Java 8+?','I understand HashMap is not thread-safe, and Hashtable locks the entire map. How does Java 8+ ConcurrentHashMap implement bucket-level synchronization with CAS?',1,'RESOLVED')",
                    "INSERT INTO doubt_questions (id,author_id,skill_id,category,title,description,accepted_answer_id,status) VALUES (2,6,5,'Computer Science Core','Difference between BFS and DFS for finding the shortest path in unweighted vs weighted graphs?','Can someone clarify when BFS guarantees the shortest path, and when we should switch to Dijkstra or Bellman-Ford?',NULL,'OPEN')",
                    "INSERT INTO doubt_questions (id,author_id,skill_id,category,title,description,accepted_answer_id,status) VALUES (3,5,3,'Databases','When should composite indexes be chosen over multiple single-column indexes in MySQL?','In an e-commerce database with queries filtering on both user_id and created_at, should I create two separate indexes or one composite index?',NULL,'OPEN')",
                    "INSERT INTO doubt_answers (id,question_id,author_id,answer_text,is_accepted) VALUES (1,1,2,'In Java 8+, ConcurrentHashMap uses CAS for empty bucket insertion and synchronized blocks on individual head nodes for occupied buckets.',TRUE)",
                    "INSERT INTO doubt_answers (id,question_id,author_id,answer_text,is_accepted) VALUES (2,2,4,'BFS guarantees the shortest path in unweighted graphs because it explores vertices layer by layer. For weighted graphs with non-negative weights, use Dijkstra instead.',FALSE)",
                    "INSERT INTO resources (id,uploader_id,title,description,category,tags,stored_filename,original_filename,file_size,mime_type,download_count,status) VALUES (1,2,'Java Concurrency & Multithreading Comprehensive Cheatsheet','A deep dive into Java memory models, thread pools, locks, and atomic variables.','Notes','Java, Concurrency, JVM, MultiThreading','res_java_concurrency.pdf','Java_Concurrency_Guide.pdf',2048500,'application/pdf',38,'APPROVED')",
                    "INSERT INTO resources (id,uploader_id,title,description,category,tags,stored_filename,original_filename,file_size,mime_type,download_count,status) VALUES (2,3,'AWS SDE-II Interview Experience & System Design Breakdown','Detailed breakdown of system design rounds, distributed cache questions, and leadership principles.','Interview Experience','AWS, SystemDesign, Interview, Cloud','res_aws_interview.pdf','AWS_SDE2_Interview_Exp.pdf',1450200,'application/pdf',84,'APPROVED')",
                    "INSERT INTO resources (id,uploader_id,title,description,category,tags,stored_filename,original_filename,file_size,mime_type,download_count,status) VALUES (3,4,'Data Structures & Algorithms Semester Midterm Solved Papers','Full solutions with complexity analyses for trees, graphs, sorting, and dynamic programming.','Question Paper','DSA, Algorithms, Exams, Solutions','res_dsa_exam_solutions.pdf','DSA_Midterm_Solved_2024.pdf',3120000,'application/pdf',52,'APPROVED')",
                    "INSERT INTO projects (id,creator_id,title,description,category,required_skills,available_positions,filled_positions,status) VALUES (1,2,'CampusConnect Mobile Companion App','Developing a lightweight cross-platform Flutter/React-Native companion app for push notifications and instant chat.','Mobile & Web','React Native, Flutter, REST API',2,0,'OPEN')",
                    "INSERT INTO projects (id,creator_id,title,description,category,required_skills,available_positions,filled_positions,status) VALUES (2,4,'Automated Medical Image Classifier using Vision Transformers','An AI research project analyzing chest X-rays using hybrid ViT architectures. Looking for collaborators.','Artificial Intelligence','PyTorch, Python, Computer Vision, Transformers',2,1,'OPEN')",
                    "INSERT INTO projects (id,creator_id,title,description,category,required_skills,available_positions,filled_positions,status) VALUES (3,5,'Peer Study Group Organizer & Resource Indexer','A collaborative desktop tool for organizing course notes and generating flashcards.','Productivity Tools','Java, JavaFX, SQLite',1,1,'FILLED')",
                    "INSERT INTO project_applications (id,project_id,applicant_id,pitch,status) VALUES (1,1,5,'Hi Sarah! I have hands-on experience building mobile apps and consuming REST APIs in React Native. I would love to contribute!','PENDING')",
                    "INSERT INTO project_applications (id,project_id,applicant_id,pitch,status) VALUES (2,2,6,'I have completed coursework in Python and Linear Algebra, and want to learn PyTorch through this hands-on research project.','ACCEPTED')",
                    "INSERT INTO notifications (user_id,title,message,type,link,is_read) VALUES (5,'Session Completed','Your mentorship session on Java Programming with Sarah Jenkins has been completed. Please submit your feedback and endorsement!','SESSION','/sessions/view?id=1',FALSE)",
                    "INSERT INTO notifications (user_id,title,message,type,link,is_read) VALUES (2,'Skill Credits Earned','You earned 10 Skill Credits for successfully mentoring Alex Rivera in Java Programming.','CREDIT','/credits',FALSE)",
                    "INSERT INTO notifications (user_id,title,message,type,link,is_read) VALUES (6,'Session Confirmed','Your mentorship session with Priya Sharma on Data Structures & Algorithms is confirmed for Thursday.','SESSION','/sessions/view?id=3',TRUE)",
                    "INSERT INTO notifications (user_id,title,message,type,link,is_read) VALUES (2,'New Project Application','Alex Rivera applied to join your project: CampusConnect Mobile Companion App.','APPLICATION','/projects/view?id=1',FALSE)",
                    "INSERT INTO content_reports (reporter_id,content_type,content_id,reason,status) VALUES (5,'RESOURCE',3,'Formatting issue in section 4.2 of the solutions document, requesting clean re-upload.','PENDING')",
                    "INSERT INTO admin_audit_logs (admin_id,action,target_type,target_id,details,ip_address) VALUES (1,'SYSTEM_INITIALIZATION','SYSTEM',NULL,'Database schema and initial seed data initialized successfully.','127.0.0.1')",
                    "INSERT INTO admin_audit_logs (admin_id,action,target_type,target_id,details,ip_address) VALUES (1,'USER_ROLE_ASSIGNED','USER',2,'Assigned SENIOR role to Sarah Jenkins with verified university credentials.','127.0.0.1')",
                    "INSERT INTO admin_audit_logs (admin_id,action,target_type,target_id,details,ip_address) VALUES (1,'CREDIT_POLICY_UPDATE','POLICY',NULL,'Initial session booking cost set to 10 credits; mentor completion reward set to 10 credits.','127.0.0.1')"
            };

            for (String query : remaining) {
                st.executeUpdate(query);
            }
        }

        ctx.log("CampusConnect: All seed data inserted.");
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("'", "''");
    }
}

