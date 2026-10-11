-- Run this script while connected to the PostgreSQL server as a superuser (for example, postgres).
-- It is safe to re-run if the roles and databases already exist.

DO $$ BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'auth') THEN
        CREATE ROLE auth LOGIN PASSWORD 'auth';
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'user') THEN
        CREATE ROLE "user" LOGIN PASSWORD 'user';
    END IF;
END $$;

SELECT 'CREATE DATABASE authdb OWNER auth'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'authdb')\gexec
SELECT 'CREATE DATABASE userdb OWNER "user"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'userdb')\gexec

GRANT ALL PRIVILEGES ON DATABASE authdb TO auth;
GRANT ALL PRIVILEGES ON DATABASE userdb TO "user";
