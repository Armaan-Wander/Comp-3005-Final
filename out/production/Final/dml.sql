INSERT INTO Person (Username, Password, Name, AccountType, Height, Weight, Age)
VALUES
('Alice', 'password', 'Alice', 'member', 1.60, 50, 20),
('Bob', 'password', 'Bob', 'member', 1.70, 60, 25),
('Charlie', 'password', 'Charlie', 'member', 1.80, 70, 30),
('David', 'password', 'David', 'member', 1.90, 80, 35),
('Eve', 'password', 'Eve', 'trainer', 2.00, 90, 40),
('Fiona', 'password', 'Fiona', 'trainer', 2.10, 100, 19),
('Grace', 'password', 'Grace', 'admin', 2.20, 110, 45);

INSERT INTO Member (PersonID, FitnessGoals)
VALUES
(1, 'Lose weight'),
(2, 'Gain muscle'),
(3, 'Get fit'),
(4, 'Stay healthy');

INSERT INTO Trainer (PersonID, Specialization)
VALUES
(5, 'Weight loss'),
(6, 'Bodybuilding');

INSERT INTO Routine (MemberID, RoutineName, Description, Duration)
VALUES
(1, 'Cardio', 'Run on treadmill', '30 minutes'),
(2, 'Strength', 'Lift weights', '1 hour'),
(3, 'Flexibility', 'Stretch and yoga', '45 minutes'),
(4, 'Endurance', 'Swim laps', '2 hours');

INSERT INTO Room (RoomName, Capacity)
VALUES
('Room 1', 10),
('Room 2', 20),
('Room 3', 30);

INSERT INTO Billing (MemberID)
VALUES
(1),
(2),
(3),
(4);


