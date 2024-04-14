
CREATE TABLE Person (
    PersonID SERIAL PRIMARY KEY,
    Username VARCHAR(255) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    AccountType VARCHAR(255) NOT NULL,
    Height DECIMAL(5, 2) DEFAULT 0.0,
    Weight DECIMAL(5, 2) DEFAULT 0.0,
    Age INT,
    --Defaults to NULL because the program will assign a schedule to each person
    Schedule INTEGER[5][8] DEFAULT NULL
);

CREATE TABLE Member (
    MemberID SERIAL PRIMARY KEY,
    PersonID INT REFERENCES Person(PersonID),
    FitnessGoals TEXT,
    FitnessAchievements TEXT DEFAULT 'No achievements yet'
);



CREATE TABLE Trainer (
    TrainerID SERIAL PRIMARY KEY,
    PersonID INT REFERENCES Person(PersonID),
    Specialization TEXT NOT NULL

);

CREATE TABLE PersonalSession (

    PersonalSessionID SERIAL PRIMARY KEY,
    TrainerID INT REFERENCES Trainer(TrainerID),
    MemberID INT REFERENCES Member(MemberID),
    Weekday INT NOT NULL,
    Time INT NOT NULL

);

CREATE TABLE Routine (
    RoutineID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Member(MemberID),
    RoutineName VARCHAR(255) NOT NULL,
    Description TEXT NOT NULL,
    Duration TEXT DEFAULT 0,
    TimesCompleted INT DEFAULT 0
);

CREATE TABLE Room (
    RoomID SERIAL PRIMARY KEY,
    RoomName VARCHAR(255) NOT NULL,
    Capacity INT,
    --Defaults to NULL because the program will assign a schedule to each room
    Schedule INTEGER[5][8] DEFAULT NULL
);




CREATE TABLE Class (
    ClassID SERIAL PRIMARY KEY,
    PersonID INT REFERENCES Person(PersonID),
    RoomID INT REFERENCES Room(RoomID),
    ClassName VARCHAR(255) NOT NULL,
    Weekday INT NOT NULL,
    Time INT NOT NULL,
    NumberOfParticipants INT DEFAULT 0
);

CREATE TABLE ClassMemberBooking (

    ClassMemberBookingID SERIAL PRIMARY KEY,
    ClassID INT REFERENCES Class(ClassID),
    MemberID INT REFERENCES Member(MemberID),
    Weekday INT NOT NULL,
    Time INT NOT NULL

);

CREATE TABLE Billing (

    BillingID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Member(MemberID),
    PaymentDue DECIMAL(5, 2) DEFAULT 15.0,
    OutstandingBalance DECIMAL(5, 2) DEFAULT 0.0,
    PendingPayments DECIMAL(5, 2) DEFAULT 0.0

);
