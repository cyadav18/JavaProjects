-- Create database for login-service
CREATE DATABASE login_db;

-- Create database for task-service
CREATE DATABASE task_db;


CREATE USER login_user WITH PASSWORD 'password';
CREATE USER task_user WITH PASSWORD 'password';

CREATE DATABASE login_db OWNER login_user;
CREATE DATABASE task_db OWNER task_user;
