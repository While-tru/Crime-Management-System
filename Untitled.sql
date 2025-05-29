CREATE DATABASE if not EXISTS CrimeManagementnew;
USE CrimeManagementnew;
-- Judge Table
CREATE TABLE if not EXISTS Judge (
    judge_id INT PRIMARY KEY,
    f_name VARCHAR(50),
    l_name VARCHAR(50),
    experience INT,
    court_assigned VARCHAR(100)
);

-- Court Table
CREATE TABLE if not EXISTS Court (
    court_id INT PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    verdict VARCHAR(50)
);

-- PoliceStation Table
CREATE TABLE if not EXISTS PoliceStation (
    station_id INT PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    contact VARCHAR(15)
);

-- PoliceOfficer Table
CREATE TABLE if not EXISTS PoliceOfficer (
    officer_id INT PRIMARY KEY,
    name VARCHAR(100),
    post VARCHAR(50),
    department VARCHAR(50),
    contact VARCHAR(15),
    station_id INT,
    FOREIGN KEY (station_id) REFERENCES PoliceStation(station_id)
);

-- FIR Table
CREATE TABLE if not EXISTS FIR (
    fir_id INT PRIMARY KEY,
    description TEXT,
    date DATE,
    time TIME,
    case_id INT
);

-- Case Table
CREATE TABLE if not EXISTS CaseTable (
    case_id INT PRIMARY KEY,
    status VARCHAR(50),
    date_started DATE,
    date_closed DATE,
    duration INT
);

-- Crime Table
CREATE TABLE if not EXISTS Crime (
    crime_id INT PRIMARY KEY,
    type VARCHAR(100),
    date DATE,
    status VARCHAR(50),
    location VARCHAR(100),
    case_id INT,
    FOREIGN KEY (case_id) REFERENCES CaseTable(case_id)
);

-- Criminal Table
CREATE TABLE if not EXISTS Criminal (
    criminal_id INT PRIMARY KEY,
    f_name VARCHAR(50),
    l_name VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    record_status VARCHAR(50),
    address VARCHAR(255),
    city VARCHAR(100),
    pincode VARCHAR(10),
    house_no VARCHAR(10)
);

-- Jail Table
CREATE TABLE if not EXISTS Jail (
    jail_id INT PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    capacity INT
);

-- Victim Table
CREATE TABLE if not EXISTS Victim (
    victim_id INT PRIMARY KEY,
    f_name VARCHAR(50),
    l_name VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    contact VARCHAR(15),
    address VARCHAR(255),
    city VARCHAR(100),
    pincode VARCHAR(10),
    house_no VARCHAR(10)
);

-- Witness Table
CREATE TABLE if not EXISTS Witness (
    witness_id INT PRIMARY KEY,
    f_name VARCHAR(50),
    l_name VARCHAR(50),
    statement TEXT,
    address VARCHAR(255),
    city VARCHAR(100),
    pincode VARCHAR(10),
    house_no VARCHAR(10),
    contact VARCHAR(15)
);

-- Evidence Table
CREATE TABLE if not EXISTS Evidence (
    evidence_id INT PRIMARY KEY,
    type VARCHAR(100),
    description VARCHAR(100),
    data_collected TEXT,
    crime_id INT,
    FOREIGN KEY (crime_id) REFERENCES Crime(crime_id)
);

-- Forensic Report Table
CREATE TABLE if not EXISTS ForensicReport (
    report_id INT PRIMARY KEY,
    evidence_id INT,
    findings TEXT,
    prepared_by VARCHAR(100),
    date DATE,
    FOREIGN KEY (evidence_id) REFERENCES Evidence(evidence_id)
);


-- Record 1
-- Insert into Judge
INSERT INTO Judge VALUES (1, 'Rajesh', 'Sharma', 15, 'Delhi High Court'),
(2, 'Anita', 'Desai', 20, 'Mumbai High Court'),
(3, 'Vikram', 'Rao', 18, 'Bangalore High Court'),
(4, 'Manoj', 'Kapoor', 25, 'Chennai High Court'),
(5, 'Sunita', 'Menon', 22, 'Kolkata High Court'),
(6, 'Dinesh', 'Patel', 19, 'Hyderabad High Court');

-- Insert into Court
INSERT INTO Court VALUES (1, 'Delhi High Court', 'New Delhi', 'Pending'),
(2, 'Mumbai High Court', 'Mumbai', 'Closed'),
(3, 'Bangalore High Court', 'Bangalore', 'Ongoing'),
(4, 'Chennai High Court', 'Chennai', 'Pending'),
(5, 'Kolkata High Court', 'Kolkata', 'Ongoing'),
(6, 'Hyderabad High Court', 'Hyderabad', 'Closed');

-- Insert into PoliceStation
INSERT INTO PoliceStation VALUES (1, 'Central Police Station', 'New Delhi', '9876543210'),
(2, 'South Mumbai Police Station', 'Mumbai', '9876543211'),
(3, 'Bangalore Central Police Station', 'Bangalore', '9876543212'),
(4, 'Chennai City Police', 'Chennai', '9876543213'),
(5, 'Kolkata Metro Police', 'Kolkata', '9876543214'),
(6, 'Cyberabad Police Station', 'Hyderabad', '9876543215');

-- Insert into PoliceOfficer
INSERT INTO PoliceOfficer VALUES (1, 'Amit Kumar', 'Inspector', 'Crime Branch', '9876543210', 1),
(2, 'Suresh Nair', 'Inspector', 'Homicide', '9876543211', 2),
(3, 'Rajiv Malhotra', 'Sub-Inspector', 'Traffic', '9876543212', 3),
(4, 'Anil Sharma', 'Assistant Commissioner', 'Cyber Crime', '9876543213', 4),
(5, 'Pooja Reddy', 'Deputy Commissioner', 'Fraud Investigation', '9876543214', 5),
(6, 'Karan Joshi', 'Commissioner', 'Anti-Terror', '9876543215', 6);

-- Insert into FIR
INSERT INTO FIR VALUES (1, 'Robbery at a bank', '2024-02-15', '14:30:00', 1),
(2, 'Murder in a hotel', '2024-01-20', '22:00:00', 2),
(3, 'Hit and Run Case', '2024-02-05', '18:45:00', 3),
(4, 'Cyber Fraud Case', '2024-03-01', '11:20:00', 4),
(5, 'Kidnapping in Park Street', '2024-03-10', '09:15:00', 5),
(6, 'Terrorist Threat Call', '2024-03-15', '03:30:00', 6);

-- Insert into Case
INSERT INTO CaseTable VALUES (1, 'Open', '2024-02-15', NULL, NULL),
(2, 'Closed', '2024-01-21', '2024-02-10', 20),
(3, 'Open', '2024-02-06', NULL, NULL),
(4, 'Under Investigation', '2024-03-02', NULL, NULL),
(5, 'Pending', '2024-03-11', NULL, NULL),
(6, 'Resolved', '2024-03-16', '2024-03-18', 2);

-- Insert into Crime
INSERT INTO Crime VALUES (1, 'Robbery', '2024-02-15', 'Under Investigation', 'Connaught Place, Delhi', 1),
(2, 'Murder', '2024-01-20', 'Closed', 'Taj Hotel, Mumbai', 2),
(3, 'Hit and Run', '2024-02-05', 'Ongoing', 'MG Road, Bangalore', 3),
(4, 'Cyber Fraud', '2024-03-01', 'Under Investigation', 'Online Transaction', 4),
(5, 'Kidnapping', '2024-03-10', 'Pending', 'Park Street, Kolkata', 5),
(6, 'Terrorism', '2024-03-15', 'Resolved', 'Hyderabad Airport', 6);

-- Insert into Criminal
INSERT INTO Criminal VALUES (1, 'Rahul', 'Verma', 32, 'Male', 'Under Trial', 'Sector 15, Noida', 'Noida', '201301', '45A'),
(2, 'Santosh', 'Yadav', 45, 'Male', 'Convicted', 'Andheri, Mumbai', 'Mumbai', '400058', '34B'),
(3, 'Ravi', 'Shekhar', 28, 'Male', 'Under Trial', 'Indiranagar, Bangalore', 'Bangalore', '560038', '12D'),
(4, 'Priya', 'Mishra', 35, 'Female', 'Under Investigation', 'T. Nagar, Chennai', 'Chennai', '600017', '21A'),
(5, 'Anil', 'Roy', 50, 'Male', 'Fugitive', 'Salt Lake, Kolkata', 'Kolkata', '700091', '89F'),
(6, 'Farhan', 'Hussain', 38, 'Male', 'Arrested', 'Banjara Hills, Hyderabad', 'Hyderabad', '500034', '56G');

-- Insert into Jail
INSERT INTO Jail VALUES (1, 'Tihar Jail', 'New Delhi', 5000),
(2, 'Arthur Road Jail', 'Mumbai', 3000),
(3, 'Bangalore Central Jail', 'Bangalore', 2500),
(4, 'Puzhal Jail', 'Chennai', 2800),
(5, 'Presidency Jail', 'Kolkata', 3500),
(6, 'Chanchalguda Jail', 'Hyderabad', 3200);

-- Insert into Victim
INSERT INTO Victim VALUES (1, 'Amit', 'Singh', 40, 'Male', '9998887776', 'Karol Bagh', 'New Delhi', '110005', '78B'),
(2, 'Arthur Road Jail', 'Mumbai', 3000),
(3, 'Bangalore Central Jail', 'Bangalore', 2500),
(4, 'Puzhal Jail', 'Chennai', 2800),
(5, 'Presidency Jail', 'Kolkata', 3500),
(6, 'Chanchalguda Jail', 'Hyderabad', 3200);

-- Insert into Witness
INSERT INTO Witness VALUES (1, 'Ramesh', 'Gupta', 'I saw two men entering the bank with guns', 'Saket', 'New Delhi', '110017', '12C', '9991112223'),
(2, 'Sanjay', 'Pillai', 'I saw the suspect running away', 'Dadar', 'Mumbai', '400014', '16D', '9992221110'),
(3, 'Ramesh', 'Iyer', 'I heard screeching tires before the crash', 'Jayanagar', 'Bangalore', '560041', '19A', '9991110009'),
(4, 'Lalitha', 'Krishnan', 'I received phishing emails before fraud', 'Velachery', 'Chennai', '600042', '54C', '9990008887'),
(5, 'Arjun', 'Ghosh', 'I saw a masked man taking a child', 'Howrah', 'Kolkata', '711101', '67B', '9998887776'),
(6, 'Ahmed', 'Siddiqui', 'I overheard suspicious conversation at airport', 'Banjara Hills', 'Hyderabad', '500034', '29H', '9997776665');

-- Insert into Evidence
INSERT INTO Evidence VALUES (1, 'CCTV Footage', 'Video showing two masked men', 1),
(2, 'Knife', 'Blood-stained knife', 2),
(3, 'Car Plate', 'Broken car plate found at scene', 3),
(4, 'Bank Statements', 'Fraudulent transactions detected', 4),
(5, 'CCTV Clip', 'Footage of child being kidnapped', 5),
(6, 'Phone Recording', 'Recorded terrorist threat', 6);

-- Insert into ForensicReport
INSERT INTO ForensicReport VALUES (1, 1, 'Analysis of video footage', 'Dr. Mehta', '2024-02-16'),
(2, 2, 'Fingerprint analysis on knife', 'Dr. Gupta', '2024-01-22'),
(3, 3, 'Vehicle match report', 'Dr. Reddy', '2024-02-07'),
(4, 4, 'Cyber trail analysis', 'Dr. Nair', '2024-03-02'),
(5, 5, 'Facial recognition on CCTV', 'Dr. Banerjee', '2024-03-11'),
(6, 6, 'Audio analysis of threat call', 'Dr. Khan', '2024-03-16');