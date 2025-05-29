-- Create the criminal table
CREATE TABLE IF NOT EXISTS criminal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    crime_type VARCHAR(100),
    status VARCHAR(50),
    arrest_date DATE,
    jail_name VARCHAR(100),
    case_ids VARCHAR(255)
);

-- Insert some sample data
INSERT INTO criminal (name, crime_type, status, arrest_date, jail_name, case_ids)
VALUES 
('John Doe', 'Theft', 'In Custody', '2023-01-15', 'Central Jail', '1,3'),
('Jane Smith', 'Fraud', 'Released', '2022-11-20', NULL, '2'),
('Robert Johnson', 'Assault', 'Wanted', NULL, NULL, '4'),
('Michael Brown', 'Robbery', 'Under Trial', '2023-03-10', 'State Prison', '5,6');