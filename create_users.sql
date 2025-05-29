USE CrimeManagement;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('JUDGE', 'POLICE', 'FORENSIC') NOT NULL
);

-- Insert sample users
INSERT INTO users (username, password, role) VALUES
('judge1', 'judge123', 'JUDGE'),
('police1', 'police123', 'POLICE'),
('forensic1', 'forensic123', 'FORENSIC'); 