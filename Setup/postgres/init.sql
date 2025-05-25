-- Create database for login-service
CREATE DATABASE login_db;

-- Create database for task-service
CREATE DATABASE task_db;


CREATE USER login_user WITH PASSWORD 'password';
CREATE USER task_user WITH PASSWORD 'password';

CREATE DATABASE login_db OWNER login_user;
CREATE DATABASE task_db OWNER task_user;

-- Switch to login_db and setup schema
\connect login_db;

-- Create permissions table
CREATE TABLE IF NOT EXISTS public.permissions (
    created_at timestamptz NOT NULL,
    id BIGSERIAL PRIMARY KEY,
    updated_at timestamptz NOT NULL,
    description VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT permissions_name_key UNIQUE (name)
);

-- Insert initial permissions
INSERT INTO public.permissions (created_at, updated_at, description, name) VALUES
  (now(), now(), 'CREATE_TASK', 'CREATE_TASK'),
  (now(), now(), 'EDIT_TASK', 'EDIT_TASK'),
  (now(), now(), 'DELETE_TASK', 'DELETE_TASK'),
  (now(), now(), 'VIEW_TASK', 'VIEW_TASK'),
  (now(), now(), 'COMMENT_TASK', 'COMMENT_TASK'),
  (now(), now(), 'ASSIGN_TASK', 'ASSIGN_TASK'),
  (now(), now(), 'CLOSE_TASK', 'CLOSE_TASK')
ON CONFLICT (name) DO NOTHING;