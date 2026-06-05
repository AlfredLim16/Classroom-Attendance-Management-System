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