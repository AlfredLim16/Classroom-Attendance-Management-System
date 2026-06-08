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

INSERT INTO Program (programName) VALUES
('BS Information Technology'),
('BS Industrial Engineering'),
('BS Computer Engineering');

INSERT INTO Semester (semesterName, schoolYear, startDate, endDate) VALUES
('1st Semester', '2025-2026', '2025-08-15', '2025-12-20'),
('2nd Semester', '2025-2026', '2026-01-10', '2026-05-30');

-- BSIT Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(1, 1, 'BSIT 1-1'), (1, 1, 'BSIT 1-2'),
(1, 2, 'BSIT 2-1'), (1, 2, 'BSIT 2-2'),
(1, 3, 'BSIT 3-1'), (1, 3, 'BSIT 3-2'),
(1, 4, 'BSIT 4-1'), (1, 4, 'BSIT 4-2');

-- BSIE Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(2, 1, 'BSIE 1-1'), (2, 1, 'BSIE 1-2'),
(2, 2, 'BSIE 2-1'), (2, 2, 'BSIE 2-2'),
(2, 3, 'BSIE 3-1'), (2, 3, 'BSIE 3-2'),
(2, 4, 'BSIE 4-1'), (2, 4, 'BSIE 4-2');

-- BSCpE Sections
INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES
(3, 1, 'BSCpE 1-1'), (3, 1, 'BSCpE 1-2'),
(3, 2, 'BSCpE 2-1'), (3, 2, 'BSCpE 2-2'),
(3, 3, 'BSCpE 3-1'), (3, 3, 'BSCpE 3-2'),
(3, 4, 'BSCpE 4-1'), (3, 4, 'BSCpE 4-2');

-- USERS
-- userId 1 = admin
-- userId 2-8  = professors (7 professors)
-- userId 9-40 = students (32 students)
-- userId 41-43 = secretaries (3 secretaries, reuse student accounts)

-- Admin
INSERT INTO User (userName, userPassword, roleId) VALUES
('admin', 'admin123', 1);

-- Professors (userId 2-8)
INSERT INTO User (userName, userPassword, roleId) VALUES
('prof.juan.delacruz',   'pass123', 2),   -- userId 2
('prof.maria.santos',    'pass123', 2),   -- userId 3
('prof.pedro.reyes',     'pass123', 2),   -- userId 4
('prof.ana.garcia',      'pass123', 2),   -- userId 5
('prof.robert.jose',     'pass123', 2),   -- userId 6
('prof.carmen.torres',   'pass123', 2),   -- userId 7
('prof.jose.mendoza',    'pass123', 2);   -- userId 8

-- Students (userId 9-40)
INSERT INTO User (userName, userPassword, roleId) VALUES
('student.andres.bonifacio',    'student123', 3),   -- userId 9
('student.jose.rizal',          'student123', 3),   -- userId 10
('student.emilio.aguinaldo',    'student123', 3),   -- userId 11
('student.apolinario.mabini',   'student123', 3),   -- userId 12
('student.melchora.aquino',     'student123', 3),   -- userId 13
('student.gabriela.silang',     'student123', 3),   -- userId 14
('student.gregorio.delpilar',   'student123', 3),   -- userId 15
('student.antonio.luna',        'student123', 3),   -- userId 16
('student.marcelo.delpilar',    'student123', 3),   -- userId 17
('student.juan.luna',           'student123', 3),   -- userId 18
('student.graciano.lopezjaena', 'student123', 3),   -- userId 19
('student.pedro.paterno',       'student123', 3),   -- userId 20
('student.felipe.agoncillo',    'student123', 3),   -- userId 21
('student.sergio.osmena',       'student123', 3),   -- userId 22
('student.manuel.quezon',       'student123', 3),   -- userId 23
('student.emilio.jacinto',      'student123', 3),   -- userId 24
('student.ramon.magsaysay',     'student123', 3),   -- userId 25
('student.carlos.garcia',       'student123', 3),   -- userId 26
('student.diosdado.macapagal',  'student123', 3),   -- userId 27
('student.ferdinand.marcos',    'student123', 3),   -- userId 28
('student.corazon.aquino',      'student123', 3),   -- userId 29
('student.fidel.ramos',         'student123', 3),   -- userId 30
('student.joseph.estrada',      'student123', 3),   -- userId 31
('student.gloria.arroyo',       'student123', 3),   -- userId 32
('student.benigno.aquino',      'student123', 3),   -- userId 33
('student.rodrigo.duterte',     'student123', 3),   -- userId 34
('student.manuel.roxas',        'student123', 3),   -- userId 35
('student.gilberto.teodoro',    'student123', 3),   -- userId 36
('student.panfilo.lacson',      'student123', 3),   -- userId 37
('student.richard.gordon',      'student123', 3),   -- userId 38
('student.eddie.villanueva',    'student123', 3),   -- userId 39
('student.loren.legarda',       'student123', 3);   -- userId 40

-- Secretaries get a separate User account with the secretary role
INSERT INTO User (userName, userPassword, roleId) VALUES
('sec.andres.bonifacio',  'sec123', 4),   -- userId 41
('sec.melchora.aquino',   'sec123', 4),   -- userId 42
('sec.marcelo.delpilar',  'sec123', 4);   -- userId 43

-- Professors
INSERT INTO Professor (userId, firstName, middleName, lastName, professorTypeId) VALUES
(2, 'Juan',    'M.', 'Dela Cruz', 2),   -- professorId 1
(3, 'Maria',   'L.', 'Santos',    3),   -- professorId 2
(4, 'Pedro',   'R.', 'Reyes',     2),   -- professorId 3
(5, 'Ana',     'S.', 'Garcia',    2),   -- professorId 4
(6, 'Robert',  'T.', 'Jose',      3),   -- professorId 5
(7, 'Carmen',  'D.', 'Torres',    2),   -- professorId 6
(8, 'Jose',    'A.', 'Mendoza',   3);   -- professorId 7

-- Students
-- BSIT 1-1 (sectionId 1)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(9,  '2025-0001-IT-1', 'Andres',    'A.', 'Bonifacio', 1, 1, 1),
(10, '2025-0002-IT-1', 'Jose',      'P.', 'Rizal',     1, 1, 1),
(11, '2025-0003-IT-1', 'Emilio',    'F.', 'Aguinaldo', 1, 1, 1),
(12, '2025-0004-IT-1', 'Apolinario','M.', 'Mabini',    1, 1, 1);

-- BSIT 1-2 (sectionId 2)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(13, '2025-0005-IT-2', 'Melchora',  'A.', 'Aquino',    1, 1, 2),
(14, '2025-0006-IT-2', 'Gabriela',  'C.', 'Silang',    1, 1, 2),
(15, '2025-0007-IT-2', 'Gregorio',  'H.', 'Del Pilar', 1, 1, 2),
(16, '2025-0008-IT-2', 'Antonio',   'L.', 'Luna',      1, 1, 2);

-- BSIT 2-1 (sectionId 3)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(17, '2025-0009-IT-3', 'Marcelo',   'H.', 'Del Pilar',    1, 2, 3),
(18, '2025-0010-IT-3', 'Juan',      'D.', 'Luna',         1, 2, 3),
(19, '2025-0011-IT-3', 'Graciano',  'J.', 'Lopez Jaena',  1, 2, 3),
(20, '2025-0012-IT-3', 'Pedro',     'A.', 'Paterno',      1, 2, 3);

-- BSIT 2-2 (sectionId 4)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(21, '2025-0013-IT-4', 'Felipe',    'B.', 'Agoncillo', 1, 2, 4),
(22, '2025-0014-IT-4', 'Sergio',    'O.', 'Osmena',    1, 2, 4),
(23, '2025-0015-IT-4', 'Manuel',    'L.', 'Quezon',    1, 2, 4),
(24, '2025-0016-IT-4', 'Emilio',    'J.', 'Jacinto',   1, 2, 4);

-- BSIE 1-1 (sectionId 9)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(25, '2025-0017-IE-1', 'Ramon',     'M.', 'Magsaysay', 2, 1, 9),
(26, '2025-0018-IE-1', 'Carlos',    'P.', 'Garcia',    2, 1, 9),
(27, '2025-0019-IE-1', 'Diosdado',  'P.', 'Macapagal', 2, 1, 9),
(28, '2025-0020-IE-1', 'Ferdinand', 'E.', 'Marcos',    2, 1, 9);

-- BSIE 1-2 (sectionId 10)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(29, '2025-0021-IE-2', 'Corazon',   'C.', 'Aquino',  2, 1, 10),
(30, '2025-0022-IE-2', 'Fidel',     'V.', 'Ramos',   2, 1, 10),
(31, '2025-0023-IE-2', 'Joseph',    'E.', 'Estrada', 2, 1, 10),
(32, '2025-0024-IE-2', 'Gloria',    'M.', 'Arroyo',  2, 1, 10);

-- BSCpE 1-1 (sectionId 17)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(33, '2025-0025-CE-1', 'Benigno',   'S.', 'Aquino III', 3, 1, 17),
(34, '2025-0026-CE-1', 'Rodrigo',   'R.', 'Duterte',    3, 1, 17),
(35, '2025-0027-CE-1', 'Manuel',    'A.', 'Roxas',      3, 1, 17),
(36, '2025-0028-CE-1', 'Gilberto',  'C.', 'Teodoro',    3, 1, 17);

-- BSCpE 1-2 (sectionId 18)
INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES
(37, '2025-0029-CE-2', 'Panfilo',   'M.', 'Lacson',     3, 1, 18),
(38, '2025-0030-CE-2', 'Richard',   'J.', 'Gordon',     3, 1, 18),
(39, '2025-0031-CE-2', 'Eddie',     'T.', 'Villanueva', 3, 1, 18),
(40, '2025-0032-CE-2', 'Loren',     'B.', 'Legarda',    3, 1, 18);


-- SECRETARIES
-- studentId 1 = Andres Bonifacio  BSIT 1-1
-- studentId 5 = Melchora Aquino   BSIT 1-2
-- studentId 9 = Marcelo Del Pilar BSIT 2-1
INSERT INTO Secretary (studentId, sectionId) VALUES
(1, 1),
(5, 2),
(9, 3);


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

INSERT INTO SchoolEvent (eventName, eventDate) VALUES
('University Week',  '2025-09-15'),
('Christmas Party',  '2025-12-18'),
('Career Fair',      '2026-02-20'),
('Workshop',         '2025-10-10'),
('Academic Week',    '2025-11-05');

INSERT INTO ProfessorCourse (professorId, courseId, semesterId) VALUES
(1, 1,  1),   -- Dela Cruz IT101
(1, 2,  1),   -- Dela Cruz IT102
(1, 4,  1),   -- Dela Cruz IT201
(2, 11, 1),   -- Santos IE101
(2, 12, 1),   -- Santos IE102
(3, 21, 1),   -- Reyes CpE101
(3, 22, 1),   -- Reyes CpE102
(4, 3,  1),   -- Garcia IT103
(4, 5,  1),   -- Garcia IT202
(5, 13, 1),   -- Jose IE103
(5, 14, 1),   -- Jose IE201
(6, 23, 1),   -- Torres CpE103
(6, 24, 1),   -- Torres CpE201
(7, 6,  1),   -- Mendoza IT203
(7, 25, 1);   -- Mendoza CpE202

INSERT INTO ProfessorSection (professorId, sectionId, semesterId, isProfessorRecording) VALUES
-- BSIT
(1, 1, 1, TRUE),    -- Dela Cruz BSIT 1-1
(1, 2, 1, FALSE),   -- Dela Cruz BSIT 1-2
(1, 3, 1, TRUE),    -- Dela Cruz BSIT 2-1
(1, 4, 1, FALSE),   -- Dela Cruz BSIT 2-2
(4, 1, 1, FALSE),   -- Garcia BSIT 1-1
(4, 2, 1, TRUE),    -- Garcia BSIT 1-2
(7, 3, 1, FALSE),   -- Mendoza BSIT 2-1
(7, 4, 1, TRUE),    -- Mendoza BSIT 2-2

-- BSIE
(2, 9,  1, TRUE),   -- Santos BSIE 1-1
(2, 10, 1, FALSE),  -- Santos BSIE 1-2
(2, 11, 1, TRUE),   -- Santos BSIE 2-1
(2, 12, 1, FALSE),  -- Santos BSIE 2-2
(5, 9,  1, FALSE),  -- Jose BSIE 1-1
(5, 10, 1, TRUE),   -- Jose BSIE 1-2
(5, 11, 1, FALSE),  -- Jose BSIE 2-1
(5, 12, 1, TRUE),   -- Jose BSIE 2-2
-- BSCpE
(3, 17, 1, TRUE),   -- Reyes BSCpE 1-1
(3, 18, 1, FALSE),  -- Reyes BSCpE 1-2
(3, 19, 1, TRUE),   -- Reyes BSCpE 2-1
(3, 20, 1, FALSE),  -- Reyes BSCpE 2-2
(6, 17, 1, FALSE),  -- Torres BSCpE 1-1
(6, 18, 1, TRUE),   -- Torres BSCpE 1-2
(6, 19, 1, FALSE),  -- Torres BSCpE 2-1
(6, 20, 1, TRUE);   -- Torres BSCpE 2-2

INSERT INTO StudentCourse (studentId, courseId, semesterId) VALUES
-- BSIT 1-1: IT101, IT102, IT103
(1, 1, 1), (2, 1, 1), (3, 1, 1), (4, 1, 1),
(1, 2, 1), (2, 2, 1), (3, 2, 1), (4, 2, 1),
(1, 3, 1), (2, 3, 1), (3, 3, 1), (4, 3, 1),

-- BSIT 1-2: IT101, IT102, IT103
(5, 1, 1), (6, 1, 1), (7, 1, 1), (8, 1, 1),
(5, 2, 1), (6, 2, 1), (7, 2, 1), (8, 2, 1),
(5, 3, 1), (6, 3, 1), (7, 3, 1), (8, 3, 1),

-- BSIT 2-1: IT201, IT202, IT203
(9, 4, 1), (10, 4, 1), (11, 4, 1), (12, 4, 1),
(9, 5, 1), (10, 5, 1), (11, 5, 1), (12, 5, 1),
(9, 6, 1), (10, 6, 1), (11, 6, 1), (12, 6, 1),

-- BSIT 2-2: IT201, IT202, IT203
(13, 4, 1), (14, 4, 1), (15, 4, 1), (16, 4, 1),
(13, 5, 1), (14, 5, 1), (15, 5, 1), (16, 5, 1),
(13, 6, 1), (14, 6, 1), (15, 6, 1), (16, 6, 1),

-- BSIE 1-1: IE101, IE102, IE103
(17, 11, 1), (18, 11, 1), (19, 11, 1), (20, 11, 1),
(17, 12, 1), (18, 12, 1), (19, 12, 1), (20, 12, 1),
(17, 13, 1), (18, 13, 1), (19, 13, 1), (20, 13, 1),

-- BSIE 1-2: IE101, IE102, IE103
(21, 11, 1), (22, 11, 1), (23, 11, 1), (24, 11, 1),
(21, 12, 1), (22, 12, 1), (23, 12, 1), (24, 12, 1),
(21, 13, 1), (22, 13, 1), (23, 13, 1), (24, 13, 1),

-- BSCpE 1-1: CpE101, CpE102, CpE103
(25, 21, 1), (26, 21, 1), (27, 21, 1), (28, 21, 1),
(25, 22, 1), (26, 22, 1), (27, 22, 1), (28, 22, 1),
(25, 23, 1), (26, 23, 1), (27, 23, 1), (28, 23, 1),

-- BSCpE 1-2: CpE101, CpE102, CpE103
(29, 21, 1), (30, 21, 1), (31, 21, 1), (32, 21, 1),
(29, 22, 1), (30, 22, 1), (31, 22, 1), (32, 22, 1),
(29, 23, 1), (30, 23, 1), (31, 23, 1), (32, 23, 1);


INSERT INTO ClassSession (courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId) VALUES
-- BSIT 1-1: IT101 (Dela Cruz)
(1, 1, 1, '2025-08-18', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-20', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-25', '08:00:00', '10:00:00', 1, NULL),
(1, 1, 1, '2025-08-27', '08:00:00', '10:00:00', 1, NULL),

-- BSIT 1-1: IT102 (Dela Cruz)
(2, 1, 1, '2025-08-19', '13:00:00', '15:00:00', 1, NULL),
(2, 1, 1, '2025-08-21', '13:00:00', '15:00:00', 1, NULL),
(2, 1, 1, '2025-08-26', '13:00:00', '15:00:00', 1, NULL),

-- BSIT 1-1: IT103 (Garcia)
(3, 1, 4, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(3, 1, 4, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSIT 1-2: IT101 (Dela Cruz)
(1, 2, 1, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(1, 2, 1, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSIT 1-2: IT102 (Dela Cruz)
(2, 2, 1, '2025-08-19', '15:00:00', '17:00:00', 1, NULL),
(2, 2, 1, '2025-08-21', '15:00:00', '17:00:00', 1, NULL),

-- BSIT 2-1: IT201 (Dela Cruz)
(4, 3, 1, '2025-08-18', '13:00:00', '15:00:00', 1, NULL),
(4, 3, 1, '2025-08-20', '13:00:00', '15:00:00', 1, NULL),

-- BSIT 2-1: IT202 (Garcia)
(5, 3, 4, '2025-08-19', '08:00:00', '10:00:00', 1, NULL),
(5, 3, 4, '2025-08-21', '08:00:00', '10:00:00', 1, NULL),

-- BSIT 2-2: IT201 (Dela Cruz)
(4, 4, 1, '2025-08-18', '15:00:00', '17:00:00', 1, NULL),
(4, 4, 1, '2025-08-20', '15:00:00', '17:00:00', 1, NULL),

-- BSIE 1-1: IE101 (Santos)
(11, 9, 2, '2025-08-18', '08:00:00', '10:00:00', 1, NULL),
(11, 9, 2, '2025-08-20', '08:00:00', '10:00:00', 1, NULL),

-- BSIE 1-1: IE102 (Santos)
(12, 9, 2, '2025-08-19', '10:00:00', '12:00:00', 1, NULL),
(12, 9, 2, '2025-08-21', '10:00:00', '12:00:00', 1, NULL),

-- BSIE 1-2: IE101 (Santos)
(11, 10, 2, '2025-08-18', '10:00:00', '12:00:00', 1, NULL),
(11, 10, 2, '2025-08-20', '10:00:00', '12:00:00', 1, NULL),

-- BSCpE 1-1: CpE101 (Reyes)
(21, 17, 3, '2025-08-18', '13:00:00', '15:00:00', 1, NULL),
(21, 17, 3, '2025-08-20', '13:00:00', '15:00:00', 1, NULL),

-- BSCpE 1-1: CpE102 (Reyes)
(22, 17, 3, '2025-08-19', '08:00:00', '10:00:00', 1, NULL),
(22, 17, 3, '2025-08-21', '08:00:00', '10:00:00', 1, NULL),

-- BSCpE 1-2: CpE101 (Reyes)
(21, 18, 3, '2025-08-18', '15:00:00', '17:00:00', 1, NULL),
(21, 18, 3, '2025-08-20', '15:00:00', '17:00:00', 1, NULL),

-- School event session
(1, 1, 1, '2025-09-15', '08:00:00', '10:00:00', 2, 1);

INSERT INTO Attendance (sessionId, studentId, statusId, recordedByUserId) VALUES
-- Session 1  (BSIT 1-1, IT101, Aug 18) - All present
(1, 1, 1, 2), (1, 2, 1, 2), (1, 3, 1, 2), (1, 4, 1, 2),

-- Session 2  (BSIT 1-1, IT101, Aug 20) - One late, one absent
(2, 1, 1, 2), (2, 2, 2, 2), (2, 3, 1, 2), (2, 4, 3, 2),

-- Session 3  (BSIT 1-1, IT101, Aug 25) - One excused
(3, 1, 1, 2), (3, 2, 1, 2), (3, 3, 4, 2), (3, 4, 1, 2),

-- Session 5  (BSIT 1-1, IT102, Aug 19) - All present
(5, 1, 1, 2), (5, 2, 1, 2), (5, 3, 1, 2), (5, 4, 1, 2),

-- Session 10 (BSIT 1-2, IT101, Aug 18) - All present
(10, 5, 1, 2), (10, 6, 1, 2), (10, 7, 1, 2), (10, 8, 1, 2),

-- Session 14 (BSIT 2-1, IT201, Aug 18) - Two late
(14, 9, 1, 2), (14, 10, 2, 2), (14, 11, 2, 2), (14, 12, 1, 2),

-- Session 20 (BSIE 1-1, IE101, Aug 18) - All present
(20, 17, 1, 3), (20, 18, 1, 3), (20, 19, 1, 3), (20, 20, 1, 3),

-- Session 26 (BSCpE 1-1, CpE101, Aug 18) - One absent
(26, 25, 1, 4), (26, 26, 3, 4), (26, 27, 1, 4), (26, 28, 1, 4);


INSERT INTO AttendancePolicy (courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive) VALUES
(1,  15, 3, 5, TRUE),
(2,  15, 3, 5, TRUE),
(3,  10, 3, 5, TRUE),
(4,  15, 3, 5, TRUE),
(5,  15, 3, 5, TRUE),
(6,  10, 3, 5, TRUE),
(11, 15, 3, 5, TRUE),
(12, 15, 3, 5, TRUE),
(21, 15, 3, 5, TRUE),
(22, 10, 3, 5, TRUE);

INSERT INTO QuizLabSchedule (courseId, quizDate, quizTypeId) VALUES
(1,  '2025-09-01', 2),  -- IT101  Quiz
(1,  '2025-09-15', 1),  -- IT101  Lab
(2,  '2025-09-10', 2),  -- IT102  Quiz
(4,  '2025-09-05', 2),  -- IT201  Quiz
(11, '2025-09-08', 2),  -- IE101  Quiz
(11, '2025-09-22', 1),  -- IE101  Lab
(21, '2025-09-03', 2),  -- CpE101 Quiz
(22, '2025-09-12', 1);  -- CpE102 Lab

INSERT INTO MissedQuizFlag (studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId) VALUES
(1,  1, 2, 1, 'Student was sick, submitted med cert', '2025-09-02', 1),
(2,  2, 3, 2, 'No valid excuse provided', '2025-09-16', 1),
(4,  3, 4, 3, 'Official school event participant', '2025-09-06', 1),
(17, 5, 2, 1, 'Family emergency, submitted documents', '2025-09-09', 2),
(25, 7, 1, 1, 'Medical certificate attached', '2025-09-04', 3);

INSERT INTO ExcuseLetter (studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate) VALUES
(1,  1,  '2025-08-25', 'Fever and flu, confined at home', 'docs/medcert_0001.pdf', 2, 2, '2025-08-26 09:00:00', '2025-08-26 14:00:00'),
(4,  1,  '2025-08-20', 'Attended provincial basketball tournament', 'docs/letter_0004.pdf', 3, 2, '2025-08-21 10:00:00', '2025-08-21 16:00:00'),
(17, 11, '2025-09-08', 'Family emergency - grandmother hospitalized', 'docs/medcert_0017.pdf', 2, 3, '2025-09-09 08:00:00', '2025-09-09 12:00:00'),
(26, 21, '2025-09-03', 'Traffic accident on the way to school', 'docs/police_report_0026.pdf', 1, 4, '2025-09-04 09:30:00', NULL);