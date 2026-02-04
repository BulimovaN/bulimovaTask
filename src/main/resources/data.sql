INSERT INTO users (username, email, password, role)
VALUES ('john_doe', 'john@example.com', 'password123', 'ROLE_USER');

INSERT INTO users (username, email, password, role)
VALUES ('admin', 'admin@example.com', 'admin123', 'ROLE_ADMIN');

-- Задачи
INSERT INTO tasks (title, description, deadline, status, user_id) VALUES
                                                                      ('Complete homework', 'Finish math and science homework', '2026-12-01', 'PENDING', 1);

INSERT INTO tasks (title, description, deadline, status, user_id) VALUES
                                                                      ('Fix server', 'Resolve critical issue on production server', '2026-11-25', 'NEW', 2);