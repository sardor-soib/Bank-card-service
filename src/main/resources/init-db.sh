#!/bin/bash
set -e

# Create database if it does not exist
psql -v ON_ERROR_STOP=1 --username "postgres" <<-EOSQL
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bank-rest-service') THEN
            CREATE DATABASE "bank-rest-service";
        END IF;
    END
    \$\$;
EOSQL

# Create user and grant privileges (Optional: postgres.yaml user usually exists)
psql -v ON_ERROR_STOP=1 --username "postgres" <<-EOSQL
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'postgres') THEN
            CREATE USER postgres WITH ENCRYPTED PASSWORD '12345';
        END IF;
    END
    \$\$;
    GRANT ALL PRIVILEGES ON DATABASE "bank-rest-service" TO postgres;
EOSQL
