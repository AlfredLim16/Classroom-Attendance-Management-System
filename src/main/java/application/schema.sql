CREATE DATABASE classroom_attendance_management;
USE classroom_attendance_management;

-- Lookup Tables (Enums)
CREATE TABLE Role (
    roleId INT AUTO_INCREMENT PRIMARY KEY,
    roleName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE AttendanceStatus (
    statusId INT AUTO_INCREMENT PRIMARY KEY,
    statusName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ContextType (
    contextId INT AUTO_INCREMENT PRIMARY KEY,
    contextName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ProfessorType (
    professorTypeId INT AUTO_INCREMENT PRIMARY KEY,
    professorTypeName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE YearLevel (
    yearLevelId INT AUTO_INCREMENT PRIMARY KEY,
    yearLevelName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE QuizType (
    quizTypeId INT AUTO_INCREMENT PRIMARY KEY,
    quizTypeName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE MissedQuizStatus (
    missedQuizStatusId INT AUTO_INCREMENT PRIMARY KEY,
    missedQuizStatusName VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE DecisionType (
    decisionTypeId INT AUTO_INCREMENT PRIMARY KEY,
    decisionTypeName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE ExcuseStatus (
    excuseStatusId INT AUTO_INCREMENT PRIMARY KEY,
    excuseStatusName VARCHAR(50) NOT NULL UNIQUE
);

-- Core Tables (Entities)
CREATE TABLE Program (
    programId INT AUTO_INCREMENT PRIMARY KEY,
    programName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Semester (
    semesterId INT AUTO_INCREMENT PRIMARY KEY,
    semesterName VARCHAR(50) NOT NULL,
    schoolYear VARCHAR(20) NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL
);

CREATE TABLE Section (
    sectionId INT AUTO_INCREMENT PRIMARY KEY,
    programId INT NOT NULL,
    yearLevelId INT NOT NULL,
    sectionCode VARCHAR(20) NOT NULL,

    CONSTRAINT fk_section_program
        FOREIGN KEY (programId)
        REFERENCES Program(programId),

    CONSTRAINT fk_section_yearlevel
        FOREIGN KEY (yearLevelId)
        REFERENCES YearLevel(yearLevelId)
);

CREATE TABLE User (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(100) NOT NULL UNIQUE,
    userPassword VARCHAR(255) NOT NULL,
    roleId INT NOT NULL,

    CONSTRAINT fk_user_role
        FOREIGN KEY (roleId)
        REFERENCES Role(roleId)
);

CREATE TABLE Professor (
    professorId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    firstName VARCHAR(100) NOT NULL,
    middleName VARCHAR(100),
    lastName VARCHAR(100) NOT NULL,
    professorTypeId INT NOT NULL,

    CONSTRAINT fk_professor_user
        FOREIGN KEY (userId)
        REFERENCES User(userId),

    CONSTRAINT fk_professor_type
        FOREIGN KEY (professorTypeId)
        REFERENCES ProfessorType(professorTypeId)
);

CREATE TABLE Student (
    studentId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    studentNumber VARCHAR(50) NOT NULL UNIQUE,
    firstName VARCHAR(100) NOT NULL,
    middleName VARCHAR(100),
    lastName VARCHAR(100) NOT NULL,
    programId INT NOT NULL,
    yearLevelId INT NOT NULL,
    sectionId INT NOT NULL,

    CONSTRAINT fk_student_user
        FOREIGN KEY (userId)
        REFERENCES User(userId),

    CONSTRAINT fk_student_program
        FOREIGN KEY (programId)
        REFERENCES Program(programId),

    CONSTRAINT fk_student_yearlevel
        FOREIGN KEY (yearLevelId)
        REFERENCES YearLevel(yearLevelId),

    CONSTRAINT fk_student_section
        FOREIGN KEY (sectionId)
        REFERENCES Section(sectionId)
);

CREATE TABLE Secretary (
    secretaryId INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    sectionId INT NOT NULL,

    CONSTRAINT fk_secretary_student
        FOREIGN KEY (studentId)
        REFERENCES Student(studentId),

    CONSTRAINT fk_secretary_section
        FOREIGN KEY (sectionId)
        REFERENCES Section(sectionId)
);

CREATE TABLE Course (
    courseId INT AUTO_INCREMENT PRIMARY KEY,
    programId INT NOT NULL,
    courseCode VARCHAR(50) NOT NULL,
    courseName VARCHAR(150) NOT NULL,
    units INT NOT NULL,
    semesterId INT NOT NULL,
    yearLevelId INT NOT NULL,

    CONSTRAINT fk_course_program
        FOREIGN KEY (programId)
        REFERENCES Program(programId),

    CONSTRAINT fk_course_semester
        FOREIGN KEY (semesterId)
        REFERENCES Semester(semesterId),

    CONSTRAINT fk_course_yearlevel
        FOREIGN KEY (yearLevelId)
        REFERENCES YearLevel(yearLevelId)
);

CREATE TABLE SchoolEvent (
    eventId INT AUTO_INCREMENT PRIMARY KEY,
    eventName VARCHAR(150) NOT NULL,
    eventDate DATE NOT NULL
);

-- Junction Tables
CREATE TABLE ProfessorCourse (
    professorCourseId INT AUTO_INCREMENT PRIMARY KEY,
    professorId INT NOT NULL,
    courseId INT NOT NULL,
    semesterId INT NOT NULL,

    CONSTRAINT fk_profcourse_professor
        FOREIGN KEY (professorId)
        REFERENCES Professor(professorId),

    CONSTRAINT fk_profcourse_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId),

    CONSTRAINT fk_profcourse_semester
        FOREIGN KEY (semesterId)
        REFERENCES Semester(semesterId),

    UNIQUE (professorId, courseId, semesterId)
);

CREATE TABLE ProfessorSection (
    professorSectionId INT AUTO_INCREMENT PRIMARY KEY,
    professorId INT NOT NULL,
    sectionId INT NOT NULL,
    semesterId INT NOT NULL,
    isProfessorRecording BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_profsection_professor
        FOREIGN KEY (professorId)
        REFERENCES Professor(professorId),

    CONSTRAINT fk_profsection_section
        FOREIGN KEY (sectionId)
        REFERENCES Section(sectionId),

    CONSTRAINT fk_profsection_semester
        FOREIGN KEY (semesterId)
        REFERENCES Semester(semesterId),

    UNIQUE (professorId, sectionId, semesterId)
);

CREATE TABLE StudentCourse (
    studentCourseId INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    courseId INT NOT NULL,
    semesterId INT NOT NULL,

    CONSTRAINT fk_studentcourse_student
        FOREIGN KEY (studentId)
        REFERENCES Student(studentId),

    CONSTRAINT fk_studentcourse_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId),

    CONSTRAINT fk_studentcourse_semester
        FOREIGN KEY (semesterId)
        REFERENCES Semester(semesterId),

    UNIQUE (studentId, courseId, semesterId)
);

CREATE TABLE ClassSession (
    sessionId INT AUTO_INCREMENT PRIMARY KEY,
    courseId INT NOT NULL,
    sectionId INT NOT NULL,
    professorId INT NOT NULL,
    sessionDate DATE NOT NULL,
    startTime TIME NOT NULL,
    endTime TIME NOT NULL,
    contextId INT NOT NULL,
    eventId INT NULL,

    CONSTRAINT fk_session_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId),

    CONSTRAINT fk_session_section
        FOREIGN KEY (sectionId)
        REFERENCES Section(sectionId),

    CONSTRAINT fk_session_professor
        FOREIGN KEY (professorId)
        REFERENCES Professor(professorId),

    CONSTRAINT fk_session_context
        FOREIGN KEY (contextId)
        REFERENCES ContextType(contextId),

    CONSTRAINT fk_session_event
        FOREIGN KEY (eventId)
        REFERENCES SchoolEvent(eventId)
);

CREATE TABLE Attendance (
    attendanceId INT AUTO_INCREMENT PRIMARY KEY,
    sessionId INT NOT NULL,
    studentId INT NOT NULL,
    statusId INT NOT NULL,
    recordedByUserId INT NOT NULL,

    CONSTRAINT fk_attendance_session
        FOREIGN KEY (sessionId)
        REFERENCES ClassSession(sessionId),

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (studentId)
        REFERENCES Student(studentId),

    CONSTRAINT fk_attendance_status
        FOREIGN KEY (statusId)
        REFERENCES AttendanceStatus(statusId),

    CONSTRAINT fk_attendance_recordedby
        FOREIGN KEY (recordedByUserId)
        REFERENCES User(userId)
);

CREATE TABLE AttendancePolicy (
    policyId INT AUTO_INCREMENT PRIMARY KEY,
    courseId INT NOT NULL,
    lateThresholdMinutes INT NOT NULL,
    latesEqualToAbsent INT NOT NULL,
    absentsEqualToDropped INT NOT NULL,
    isActive BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_policy_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId)
);

CREATE TABLE QuizLabSchedule (
    quizId INT AUTO_INCREMENT PRIMARY KEY,
    courseId INT NOT NULL,
    quizDate DATE NOT NULL,
    quizTypeId INT NOT NULL,

    CONSTRAINT fk_quiz_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId),

    CONSTRAINT fk_quiz_type
        FOREIGN KEY (quizTypeId)
        REFERENCES QuizType(quizTypeId)
);

CREATE TABLE MissedQuizFlag (
    flagId INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    quizId INT NOT NULL,
    missedQuizStatusId INT NOT NULL,
    decisionTypeId INT NOT NULL,
    remarks TEXT,
    decisionDate DATE,
    decidedByProfessorId INT NOT NULL,

    CONSTRAINT fk_flag_student
        FOREIGN KEY (studentId)
        REFERENCES Student(studentId),

    CONSTRAINT fk_flag_quiz
        FOREIGN KEY (quizId)
        REFERENCES QuizLabSchedule(quizId),

    CONSTRAINT fk_flag_status
        FOREIGN KEY (missedQuizStatusId)
        REFERENCES MissedQuizStatus(missedQuizStatusId),

    CONSTRAINT fk_flag_decision
        FOREIGN KEY (decisionTypeId)
        REFERENCES DecisionType(decisionTypeId),

    CONSTRAINT fk_flag_professor
        FOREIGN KEY (decidedByProfessorId)
        REFERENCES Professor(professorId)
);

CREATE TABLE ExcuseLetter (
    excuseId INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    courseId INT NOT NULL,
    absentDate DATE NOT NULL,
    reason TEXT NOT NULL,
    supportingDocumentPath VARCHAR(255),
    excuseStatusId INT NOT NULL,
    reviewedByUserId INT,
    submittedDate DATETIME NOT NULL,
    reviewedDate DATETIME,

    CONSTRAINT fk_excuse_student
        FOREIGN KEY (studentId)
        REFERENCES Student(studentId),

    CONSTRAINT fk_excuse_course
        FOREIGN KEY (courseId)
        REFERENCES Course(courseId),

    CONSTRAINT fk_excuse_status
        FOREIGN KEY (excuseStatusId)
        REFERENCES ExcuseStatus(excuseStatusId),

    CONSTRAINT fk_excuse_reviewedby
        FOREIGN KEY (reviewedByUserId)
        REFERENCES User(userId)
);


-- Seed Data

-- 1. Attendace Status
INSERT INTO AttendanceStatus (statusName) VALUES
('Present'), ('Late'), ('Absent'), ('Excused');

-- 2. Context Types
INSERT INTO ContextType (contextName) VALUES
('Classroom'), ('School Event');

-- 3. Professor Types
INSERT INTO ProfessorType (professorTypeName) VALUES
('Faculty'), ('Full-time'), ('Part-time');

-- 4. Year Level
INSERT INTO YearLevel (yearLevelName) VALUES
('1st Year'), ('2nd Year'), ('3rd Year'), ('4th Year');

-- 5. Quiz Types
INSERT INTO QuizType (quizTypeName) VALUES
('Lab'), ('Quiz'), ('Exam');

-- 6. Excuse Status
INSERT INTO ExcuseStatus (excuseStatusName) VALUES
('Pending'), ('Approved'), ('Rejected');

-- 7. Roles
INSERT INTO Role (roleName) VALUES
('Admin'), ('Professor'), ('Student'), ('Secretary');

-- 8. Programs (BSIT, BSIE, BSCpE)
INSERT INTO Program (programName) VALUES
('BS Information Technology'),
('BS Industrial Engineering'),
('BS Computer Engineering');

-- 9. Semester
INSERT INTO Semester (semesterName, schoolYear, startDate, endDate) VALUES
('1st Semester', '2025-2026', '2025-08-15', '2025-12-20'),
('2nd Semester', '2025-2026', '2026-01-10', '2026-05-30');

-- 10. Sections (Format: [Program] [Year]-[Section])
-- BSIT Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(1, 1, 'BSIT 1-1'),
(1, 1, 'BSIT 1-2'),
(1, 2, 'BSIT 2-1'),
(1, 2, 'BSIT 2-2'),
(1, 3, 'BSIT 3-1'),
(1, 3, 'BSIT 3-2'),
(1, 4, 'BSIT 4-1'),
(1, 4, 'BSIT 4-2');

-- BSIE Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(2, 1, 'BSIE 1-1'),
(2, 1, 'BSIE 1-2'),
(2, 2, 'BSIE 2-1'),
(2, 2, 'BSIE 2-2'),
(2, 3, 'BSIE 3-1'),
(2, 3, 'BSIE 3-2'),
(2, 4, 'BSIE 4-1'),
(2, 4, 'BSIE 4-2');

-- BSCpE Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(3, 1, 'BSCpE 1-1'),
(3, 1, 'BSCpE 1-2'),
(3, 2, 'BSCpE 2-1'),
(3, 2, 'BSCpE 2-2'),
(3, 3, 'BSCpE 3-1'),
(3, 3, 'BSCpE 3-2'),
(3, 4, 'BSCpE 4-1'),
(3, 4, 'BSCpE 4-2');

-- 11. Users
-- Admin
INSERT INTO User (userName, userPassword, roleId) VALUES
('admin', 'admin123', 1);

-- Professors
INSERT INTO User (userName, userPassword, roleId) VALUES
('prof.delacruz', 'pass123', 2),
('prof.santos', 'pass123', 2),
('prof.reyes', 'pass123', 2),
('prof.garcia', 'pass123', 2),
('prof.jose', 'pass123', 2),
('prof.torres', 'pass123', 2),
('prof.mendoza', 'pass123', 2);

-- Students (Format: YYYY-XXXX-BN-0)
INSERT INTO User (userName, userPassword, roleId) VALUES
('2025-0001-BN-0', 'student123', 3),
('2025-0002-BN-0', 'student123', 3),
('2025-0003-BN-0', 'student123', 3),
('2025-0004-BN-0', 'student123', 3),
('2025-0005-BN-0', 'student123', 3),
('2025-0006-BN-0', 'student123', 3),
('2025-0007-BN-0', 'student123', 3),
('2025-0008-BN-0', 'student123', 3),
('2025-0009-BN-0', 'student123', 3),
('2025-0010-BN-0', 'student123', 3),
('2025-0011-BN-0', 'student123', 3),
('2025-0012-BN-0', 'student123', 3),
('2025-0013-BN-0', 'student123', 3),
('2025-0014-BN-0', 'student123', 3),
('2025-0015-BN-0', 'student123', 3),
('2025-0016-BN-0', 'student123', 3),
('2025-0017-BN-0', 'student123', 3),
('2025-0018-BN-0', 'student123', 3),
('2025-0019-BN-0', 'student123', 3),
('2025-0020-BN-0', 'student123', 3),
('2025-0021-BN-0', 'student123', 3),
('2025-0022-BN-0', 'student123', 3),
('2025-0023-BN-0', 'student123', 3),
('2025-0024-BN-0', 'student123', 3),
('2025-0025-BN-0', 'student123', 3),
('2025-0026-BN-0', 'student123', 3),
('2025-0027-BN-0', 'student123', 3),
('2025-0028-BN-0', 'student123', 3),
('2025-0029-BN-0', 'student123', 3),
('2025-0030-BN-0', 'student123', 3),
('2025-0031-BN-0', 'student123', 3),
('2025-0032-BN-0', 'student123', 3);

-- Secretaries
INSERT INTO User (userName, userPassword, roleId) VALUES
('sec.2025-0001-BN-0', 'sec123', 4),
('sec.2025-0005-BN-0', 'sec123', 4),
('sec.2025-0009-BN-0', 'sec123', 4);

-- 12. Professors
INSERT INTO Professor (userId, firstName, middleName, lastName, professorTypeId) VALUES
(2, 'Juan', 'M.', 'Dela Cruz', 2),
(3, 'Maria', 'L.', 'Santos', 3),
(4, 'Pedro', 'R.', 'Reyes', 2),
(5, 'Ana', 'S.', 'Garcia', 2),
(6, 'Robert', 'T.', 'Jose', 3),
(7, 'Carmen', 'D.', 'Torres', 2),
(8, 'Jose', 'A.', 'Mendoza', 3);

-- 13. Students
-- BSIT 1-1 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(9, '2025-0001-BN-0', 'Andres', 'A.', 'Bonifacio', 1, 1, 1),
(10, '2025-0002-BN-0', 'Jose', 'P.', 'Rizal', 1, 1, 1),
(11, '2025-0003-BN-0', 'Emilio', 'F.', 'Aguinaldo', 1, 1, 1),
(12, '2025-0004-BN-0', 'Apolinario', 'M.', 'Mabini', 1, 1, 1);

-- BSIT 1-2 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(13, '2025-0005-BN-0', 'Melchora', 'A.', 'Aquino', 1, 1, 2),
(14, '2025-0006-BN-0', 'Gabriela', 'C.', 'Silang', 1, 1, 2),
(15, '2025-0007-BN-0', 'Gregorio', 'H.', 'Del Pilar', 1, 1, 2),
(16, '2025-0008-BN-0', 'Antonio', 'L.', 'Luna', 1, 1, 2);

-- BSIT 2-1 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(17, '2025-0009-BN-0', 'Marcelo', 'H.', 'Del Pilar', 1, 2, 3),
(18, '2025-0010-BN-0', 'Juan', 'D.', 'Luna', 1, 2, 3),
(19, '2025-0011-BN-0', 'Graciano', 'J.', 'Lopez Jaena', 1, 2, 3),
(20, '2025-0012-BN-0', 'Pedro', 'A.', 'Paterno', 1, 2, 3);

-- BSIT 2-2 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(21, '2025-0013-BN-0', 'Felipe', 'B.', 'Agoncillo', 1, 2, 4),
(22, '2025-0014-BN-0', 'Sergio', 'O.', 'Osmeña', 1, 2, 4),
(23, '2025-0015-BN-0', 'Manuel', 'L.', 'Quezon', 1, 2, 4),
(24, '2025-0016-BN-0', 'Emilio', 'J.', 'Jacinto', 1, 2, 4);

-- BSIE 1-1 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(25, '2025-0017-BN-0', 'Ramon', 'M.', 'Magsaysay', 2, 1, 9),
(26, '2025-0018-BN-0', 'Carlos', 'P.', 'Garcia', 2, 1, 9),
(27, '2025-0019-BN-0', 'Diosdado', 'P.', 'Macapagal', 2, 1, 9),
(28, '2025-0020-BN-0', 'Ferdinand', 'E.', 'Marcos', 2, 1, 9);

-- BSIE 1-2 Students
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(29, '2025-0021-BN-0', 'Corazon', 'C.', 'Aquino', 2, 1, 10),
(30, '2025-0022-BN-0', 'Fidel', 'V.', 'Ramos', 2, 1, 10),
(31, '2025-0023-BN-0', 'Joseph', 'E.', 'Estrada', 2, 1, 10),
(32, '2025-0024-BN-0', 'Gloria', 'M.', 'Arroyo', 2, 1, 10);

-- BSCpE 1-1 (sectionId 17)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(36, '2025-0025-BN-0', 'Benigno', 'S.', 'Aquino III', 3, 1, 17),
(37, '2025-0026-BN-0', 'Rodrigo', 'R.', 'Duterte', 3, 1, 17),
(38, '2025-0027-BN-0', 'Manuel', 'A.', 'Roxas', 3, 1, 17),
(39, '2025-0028-BN-0', 'Gilberto', 'C.', 'Teodoro', 3, 1, 17);

-- BSCpE 1-2 (sectionId 18)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(40, '2025-0029-BN-0', 'Panfilo', 'M.', 'Lacson', 3, 1, 18),
(41, '2025-0030-BN-0', 'Richard', 'J.', 'Gordon', 3, 1, 18),
(42, '2025-0031-BN-0', 'Eddie', 'T.', 'Villanueva', 3, 1, 18),
(43, '2025-0032-BN-0', 'Loren', 'B.', 'Legarda', 3, 1, 18);

-- 14. Secretaries
INSERT INTO Secretary (studentId, sectionId) VALUES
(1, 1),   -- Andres is secretary of BSIT 1-1
(5, 2),   -- Melchora is secretary of BSIT 1-2
(9, 3);   -- Marcelo is secretary of BSIT 2-1

-- 15. Courses
-- BSIT Courses
INSERT INTO Course (programId, courseCode, courseName, units, semesterId, yearLevelId) VALUES
(1, 'IT101', 'Introduction to Programming', 3, 1, 1),
(1, 'IT102', 'Database Management Systems', 3, 1, 1),
(1, 'IT103', 'Web Development', 3, 1, 1),
(1, 'IT201', 'Object-Oriented Programming', 3, 1, 2),
(1, 'IT202', 'Data Structures and Algorithms', 3, 1, 2),
(1, 'IT203', 'Computer Networks', 3, 1, 2),
(1, 'IT301', 'Software Engineering', 3, 1, 3),
(1, 'IT302', 'Mobile Application Development', 3, 1, 3),
(1, 'IT401', 'Capstone Project 1', 3, 1, 4),
(1, 'IT402', 'Information Assurance and Security', 3, 1, 4);

-- BSIE Courses
INSERT INTO Course (programId, courseCode, courseName, units, semesterId, yearLevelId) VALUES
(2, 'IE101', 'Engineering Mechanics', 3, 1, 1),
(2, 'IE102', 'Work Analysis and Design', 3, 1, 1),
(2, 'IE103', 'Engineering Drawing', 3, 1, 1),
(2, 'IE201', 'Operations Research 1', 3, 1, 2),
(2, 'IE202', 'Ergonomics', 3, 1, 2),
(2, 'IE203', 'Quality Control', 3, 1, 2),
(2, 'IE301', 'Supply Chain Management', 3, 1, 3),
(2, 'IE302', 'Project Management', 3, 1, 3),
(2, 'IE401', 'Thesis 1', 3, 1, 4),
(2, 'IE402', 'Industrial Plant Design', 3, 1, 4);

-- BSCpE Courses
INSERT INTO Course (programId, courseCode, courseName, units, semesterId, yearLevelId) VALUES
(3, 'CpE101', 'Computer Fundamentals', 3, 1, 1),
(3, 'CpE102', 'Programming Logic and Design', 3, 1, 1),
(3, 'CpE103', 'Discrete Mathematics', 3, 1, 1),
(3, 'CpE201', 'Digital Logic Design', 3, 1, 2),
(3, 'CpE202', 'Microprocessors', 3, 1, 2),
(3, 'CpE203', 'Data Communications', 3, 1, 2),
(3, 'CpE301', 'Embedded Systems', 3, 1, 3),
(3, 'CpE302', 'Computer Architecture', 3, 1, 3),
(3, 'CpE401', 'Thesis 1', 3, 1, 4),
(3, 'CpE402', 'Advanced Computer Engineering', 3, 1, 4);

-- 16. SCHOOL EVENTS
INSERT INTO SchoolEvent (eventName, eventDate) VALUES
('University Week', '2025-09-15'),
('Christmas Party', '2025-12-18'),
('Career Fair', '2026-02-20'),
('Workshop', '2025-10-10'),
('Academic Week', '2025-11-05');

-- 17. PROFESSOR-COURSE ASSIGNMENTS
INSERT INTO ProfessorCourse (professorId, courseId, semesterId) VALUES
(1, 1, 1),   -- Dela Cruz teaches IT101
(1, 2, 1),   -- Dela Cruz teaches IT102
(1, 4, 1),   -- Dela Cruz teaches IT201
(2, 11, 1),  -- Santos teaches IE101
(2, 12, 1),  -- Santos teaches IE102
(3, 21, 1),  -- Reyes teaches CpE101
(3, 22, 1),  -- Reyes teaches CpE102
(4, 3, 1),   -- Garcia teaches IT103
(4, 5, 1),   -- Garcia teaches IT202
(5, 13, 1),  -- Jose teaches IE103
(5, 14, 1),  -- Jose teaches IE201
(6, 23, 1),  -- Torres teaches CpE103
(6, 24, 1),  -- Torres teaches CpE201
(7, 6, 1),   -- Mendoza teaches IT203
(7, 25, 1);  -- Mendoza teaches CpE202

-- 18. PROFESSOR-SECTION ASSIGNMENTS
INSERT INTO ProfessorSection (professorId, sectionId, semesterId, isProfessorRecording) VALUES
-- BSIT Sections
(1, 1, 1, TRUE),   -- Dela Cruz -> BSIT 1-1
(1, 2, 1, FALSE),  -- Dela Cruz -> BSIT 1-2
(1, 3, 1, TRUE),   -- Dela Cruz -> BSIT 2-1
(1, 4, 1, FALSE),  -- Dela Cruz -> BSIT 2-2
(4, 1, 1, FALSE),  -- Garcia -> BSIT 1-1
(4, 2, 1, TRUE),   -- Garcia -> BSIT 1-2
(7, 3, 1, FALSE),  -- Mendoza -> BSIT 2-1
(7, 4, 1, TRUE),   -- Mendoza -> BSIT 2-2

-- BSIE Sections
(2, 9, 1, TRUE),   -- Santos -> BSIE 1-1
(2, 10, 1, FALSE), -- Santos -> BSIE 1-2
(2, 11, 1, TRUE),  -- Santos -> BSIE 2-1
(2, 12, 1, FALSE), -- Santos -> BSIE 2-2
(5, 9, 1, FALSE),  -- Jose -> BSIE 1-1
(5, 10, 1, TRUE),  -- Jose -> BSIE 1-2
(5, 11, 1, FALSE), -- Jose -> BSIE 2-1
(5, 12, 1, TRUE),  -- Jose -> BSIE 2-2

-- BSCpE Sections
(3, 17, 1, TRUE),  -- Reyes -> BSCpE 1-1
(3, 18, 1, FALSE), -- Reyes -> BSCpE 1-2
(3, 19, 1, TRUE),  -- Reyes -> BSCpE 2-1
(3, 20, 1, FALSE), -- Reyes -> BSCpE 2-2
(6, 17, 1, FALSE), -- Torres -> BSCpE 1-1
(6, 18, 1, TRUE),  -- Torres -> BSCpE 1-2
(6, 19, 1, FALSE), -- Torres -> BSCpE 2-1
(6, 20, 1, TRUE);  -- Torres -> BSCpE 2-2

-- 19. STUDENT-COURSE ENROLLMENTS
INSERT INTO StudentCourse (studentId, courseId, semesterId) VALUES
(1, 1, 1), (2, 1, 1), (3, 1, 1), (4, 1, 1),
(1, 2, 1), (2, 2, 1), (3, 2, 1), (4, 2, 1),
(1, 3, 1), (2, 3, 1), (3, 3, 1), (4, 3, 1),

(5, 1, 1), (6, 1, 1), (7, 1, 1), (8, 1, 1),
(5, 2, 1), (6, 2, 1), (7, 2, 1), (8, 2, 1),
(5, 3, 1), (6, 3, 1), (7, 3, 1), (8, 3, 1),

(9, 4, 1), (10, 4, 1), (11, 4, 1), (12, 4, 1),
(9, 5, 1), (10, 5, 1), (11, 5, 1), (12, 5, 1),
(9, 6, 1), (10, 6, 1), (11, 6, 1), (12, 6, 1),

(13, 4, 1), (14, 4, 1), (15, 4, 1), (16, 4, 1),
(13, 5, 1), (14, 5, 1), (15, 5, 1), (16, 5, 1),
(13, 6, 1), (14, 6, 1), (15, 6, 1), (16, 6, 1),

(17, 11, 1), (18, 11, 1), (19, 11, 1), (20, 11, 1),
(17, 12, 1), (18, 12, 1), (19, 12, 1), (20, 12, 1),
(17, 13, 1), (18, 13, 1), (19, 13, 1), (20, 13, 1),

(21, 11, 1), (22, 11, 1), (23, 11, 1), (24, 11, 1),
(21, 12, 1), (22, 12, 1), (23, 12, 1), (24, 12, 1),
(21, 13, 1), (22, 13, 1), (23, 13, 1), (24, 13, 1),

(25, 21, 1), (26, 21, 1), (27, 21, 1), (28, 21, 1),
(25, 22, 1), (26, 22, 1), (27, 22, 1), (28, 22, 1),
(25, 23, 1), (26, 23, 1), (27, 23, 1), (28, 23, 1),

(29, 21, 1), (30, 21, 1), (31, 21, 1), (32, 21, 1),
(29, 22, 1), (30, 22, 1), (31, 22, 1), (32, 22, 1),
(29, 23, 1), (30, 23, 1), (31, 23, 1), (32, 23, 1);

-- 20. CLASS SESSIONS
INSERT INTO ClassSession (courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId) VALUES
-- BSIT 1-1 IT101 sessions (Dela Cruz)
(1, 1, 1, '2025-08-18', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-20', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-25', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-27', '08:00:00', '10:00:00', 1, NULL),

-- BSIT 1-1 IT102 sessions (Dela Cruz)
(2, 1, 1, '2025-08-19', '13:00:00', '15:00:00', 1, NULL),
(2, 1, 1, '2025-08-21', '13:00:00', '15:00:00', 1, NULL),
(2, 1, 1, '2025-08-26', '13:00:00', '15:00:00', 1, NULL),

-- BSIT 1-1 IT103 sessions (Garcia)
(3, 1, 4, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(3, 1, 4, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSIT 1-2 IT101 sessions (Dela Cruz)
(1, 2, 1, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(1, 2, 1, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSIT 1-2 IT102 sessions (Dela Cruz)
(2, 2, 1, '2025-08-19', '15:00:00', '17:00:00', 1, NULL),
(2, 2, 1, '2025-08-21', '15:00:00', '17:00:00', 1, NULL),

-- BSIT 2-1 IT201 sessions (Dela Cruz)
(4, 3, 1, '2025-08-18', '13:00:00', '15:00:00', 1, NULL),
(4, 3, 1, '2025-08-20', '13:00:00', '15:00:00', 1, NULL),

-- BSIT 2-1 IT202 sessions (Garcia)
(5, 3, 4, '2025-08-19', '08:00:00', '10:00:00', 1, NULL),
(5, 3, 4, '2025-08-21', '08:00:00', '10:00:00', 1, NULL),

-- BSIT 2-2 IT201 sessions (Dela Cruz)
(4, 4, 1, '2025-08-18', '15:00:00', '17:00:00', 1, NULL),
(4, 4, 1, '2025-08-20', '15:00:00', '17:00:00', 1, NULL),

-- BSIE 1-1 IE101 sessions (Santos)
(11, 9, 2, '2025-08-18', '08:00:00', '10:00:00', 1, NULL),
(11, 9, 2, '2025-08-20', '08:00:00', '10:00:00', 1, NULL),

-- BSIE 1-1 IE102 sessions (Santos)
(12, 9, 2, '2025-08-19', '10:00:00', '12:00:00', 1, NULL),
(12, 9, 2, '2025-08-21', '10:00:00', '12:00:00', 1, NULL),

-- BSIE 1-2 IE101 sessions (Santos)
(11, 10, 2, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(11, 10, 2, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSCpE 1-1 CpE101 sessions (Reyes)
(21, 17, 3, '2025-08-18', '13:00:00', '15:00:00', 1, NULL),
(21, 17, 3, '2025-08-20', '13:00:00', '15:00:00', 1, NULL),

-- BSCpE 1-1 CpE102 sessions (Reyes)
(22, 17, 3, '2025-08-19', '08:00:00', '10:00:00', 1, NULL),
(22, 17, 3, '2025-08-21', '08:00:00', '10:00:00', 1, NULL),

-- BSCpE 1-2 CpE101 sessions (Reyes)
(21, 18, 3, '2025-08-18', '15:00:00', '17:00:00', 1, NULL),
(21, 18, 3, '2025-08-20', '15:00:00', '17:00:00', 1, NULL),

-- School event session (no regular class)
(1, 1, 1, '2025-09-15', '08:00:00', '10:00:00', 2, 1);

-- 21. ATTENDANCE RECORDS
INSERT INTO Attendance (sessionId, studentId, statusId, recordedByUserId) VALUES
-- Session 1 (BSIT 1-1 IT101, Aug 18) - All present
(1, 1, 1, 2), (1, 2, 1, 2), (1, 3, 1, 2), (1, 4, 1, 2),

-- Session 2 (BSIT 1-1 IT101, Aug 20) - One late, one absent
(2, 1, 1, 2), (2, 2, 2, 2), (2, 3, 1, 2), (2, 4, 3, 2),

-- Session 3 (BSIT 1-1 IT101, Aug 25) - One excused
(3, 1, 1, 2), (3, 2, 1, 2), (3, 3, 4, 2), (3, 4, 1, 2),

-- Session 5 (BSIT 1-1 IT102, Aug 19) - All present
(5, 1, 1, 2), (5, 2, 1, 2), (5, 3, 1, 2), (5, 4, 1, 2),

-- Session 10 (BSIT 1-2 IT101, Aug 18) - All present
(10, 5, 1, 2), (10, 6, 1, 2), (10, 7, 1, 2), (10, 8, 1, 2),

-- Session 14 (BSIT 2-1 IT201, Aug 18) - Two late
(14, 9, 1, 2), (14, 10, 2, 2), (14, 11, 2, 2), (14, 12, 1, 2),

-- Session 20 (BSIE 1-1 IE101, Aug 18) - All present
(20, 17, 1, 3), (20, 18, 1, 3), (20, 19, 1, 3), (20, 20, 1, 3),

-- Session 26 (BSCpE 1-1 CpE101, Aug 18) - One absent
(26, 25, 1, 4), (26, 26, 3, 4), (26, 27, 1, 4), (26, 28, 1, 4);

-- 22. ATTENDANCE POLICIES
INSERT INTO AttendancePolicy (courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive) VALUES
(1, 15, 3, 5, TRUE),
(2, 15, 3, 5, TRUE),
(3, 10, 3, 5, TRUE),
(4, 15, 3, 5, TRUE),
(5, 15, 3, 5, TRUE),
(6, 10, 3, 5, TRUE),
(11, 15, 3, 5, TRUE),
(12, 15, 3, 5, TRUE),
(21, 15, 3, 5, TRUE),
(22, 10, 3, 5, TRUE);

-- 23. QUIZ/LAB SCHEDULES
INSERT INTO QuizLabSchedule (courseId, quizDate, quizTypeId) VALUES
(1, '2025-09-01', 2),   -- IT101 Quiz
(1, '2025-09-15', 1),   -- IT101 Lab (moved due to event)
(2, '2025-09-10', 2),   -- IT102 Quiz
(4, '2025-09-05', 2),   -- IT201 Quiz
(11, '2025-09-08', 2),  -- IE101 Quiz
(11, '2025-09-22', 1),  -- IE101 Lab
(21, '2025-09-03', 2),  -- CpE101 Quiz
(22, '2025-09-12', 1);  -- CpE102 Lab

-- 24. MISSED QUIZ STATUS & DECISION TYPES
INSERT INTO MissedQuizStatus (missedQuizStatusName) VALUES
('Pending'), ('Approved'), ('Rejected'), ('Excused');

INSERT INTO DecisionType (decisionTypeName) VALUES
('Allow Make-up'), ('Zero Score'), ('Excused Absence'), ('Consider as Present');

-- 25. MISSED QUIZ FLAGS
INSERT INTO MissedQuizFlag (studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId) VALUES
(1, 1, 2, 1, 'Student was sick, submitted med cert', '2025-09-02', 1),
(2, 2, 3, 2, 'No valid excuse provided', '2025-09-16', 1),
(4, 3, 4, 4, 'Official school event participant', '2025-09-06', 1),
(17, 5, 2, 1, 'Family emergency, submitted documents', '2025-09-09', 2),
(25, 7, 1, 1, 'Medical certificate attached', '2025-09-04', 3);

-- 26. EXCUSE LETTERS
INSERT INTO ExcuseLetter (studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate) VALUES
(1, 1, '2025-08-25', 'Fever and flu, confined at home', 'docs/medcert_0001.pdf', 2, 2, '2025-08-26 09:00:00', '2025-08-26 14:00:00'),
(4, 1, '2025-08-20', 'Attended provincial basketball tournament', 'docs/letter_0004.pdf', 3, 2, '2025-08-21 10:00:00', '2025-08-21 16:00:00'),
(17, 11, '2025-09-08', 'Family emergency - grandmother hospitalized', 'docs/medcert_0017.pdf', 2, 3, '2025-09-09 08:00:00', '2025-09-09 12:00:00'),
(26, 21, '2025-09-03', 'Traffic accident on the way to school', 'docs/police_report_0026.pdf', 1, 4, '2025-09-04 09:30:00', NULL);