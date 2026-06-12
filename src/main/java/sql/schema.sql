CREATE DATABASE classroom_attendance_management;
USE classroom_attendance_management;

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
        REFERENCES User(userId),

    CONSTRAINT uq_attendance_session_student
        UNIQUE (sessionId, studentId)
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
    decisionTypeId INT NULL,
    remarks TEXT,
    decisionDate DATE,
    decidedByProfessorId INT NULL,

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

INSERT INTO AttendanceStatus (statusName) VALUES
('Present'), ('Late'), ('Absent'), ('Excused');

INSERT INTO ContextType (contextName) VALUES
('Classroom'), ('School Event');

INSERT INTO ProfessorType (professorTypeName) VALUES
('Faculty'), ('Full-time'), ('Part-time');

INSERT INTO YearLevel (yearLevelName) VALUES
('1st Year'), ('2nd Year'), ('3rd Year'), ('4th Year');

INSERT INTO QuizType (quizTypeName) VALUES
('Lab'), ('Quiz'), ('Exam');

INSERT INTO ExcuseStatus (excuseStatusName) VALUES
('Pending'), ('Approved'), ('Rejected');

INSERT INTO MissedQuizStatus (missedQuizStatusName) VALUES
('Pending'), ('Approved'), ('Rejected'), ('Excused');

INSERT INTO DecisionType (decisionTypeName) VALUES
('Allow Make-up'), ('Zero Score'), ('Excused Absence');

INSERT INTO Role (roleName) VALUES
('Admin'), ('Professor'), ('Student'), ('Secretary');

-- Admin only because admin will create account for student, secretary(class officer) and professor.
-- Password is 'admin123' hashed with SHA-256 + random salt (format: base64salt:base64hash).
-- To reset: delete this row and re-insert using the app's create-user flow, or run GenHash.java.
INSERT INTO User (userName, userPassword, roleId) VALUES
('admin', '4bhe9OTydv/i6XNxdtDELQ==:A5Tx/zqeRUDZWj2fDX7TYx+4FctIRFRI9qzzjrgYFWA=', 1);