-- Create the case table
CREATE TABLE IF NOT EXISTS casetable (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) DEFAULT 'Untitled Case',
    description TEXT,
    status VARCHAR(50) DEFAULT 'Pending',
    date_started DATE
);

-- Create the crime table
CREATE TABLE IF NOT EXISTS crime (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(100),
    location VARCHAR(255),
    case_id INT,
    FOREIGN KEY (case_id) REFERENCES casetable(id) ON DELETE CASCADE
);

-- Insert some sample data
INSERT INTO casetable (title, description, status, date_started)
VALUES 
('Theft at Main Street', 'Investigation of theft at 123 Main Street', 'Open', '2023-01-10'),
('Fraud Case #2023-F-001', 'Corporate fraud investigation', 'Under Investigation', '2023-02-15'),
('Assault at City Park', 'Assault reported at City Park on March 5', 'Closed', '2023-03-05'),
('Robbery at First National Bank', 'Armed robbery investigation', 'Open', '2023-04-20');

-- Insert crime details
INSERT INTO crime (type, location, case_id)
VALUES 
('Theft', '123 Main Street', 1),
('Fraud', 'ABC Corporation Headquarters', 2),
('Assault', 'City Park, North Entrance', 3),
('Armed Robbery', 'First National Bank, Downtown Branch', 4);