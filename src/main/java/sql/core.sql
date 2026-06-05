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