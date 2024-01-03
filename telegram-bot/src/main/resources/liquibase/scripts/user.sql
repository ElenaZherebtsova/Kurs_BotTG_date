-- liquibase formatted sql

-- changeset elena_zh:1
CREATE TABLE users (
    id SERIAL,
    email TEXT