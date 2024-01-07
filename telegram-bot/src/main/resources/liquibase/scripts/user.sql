-- liquibase formatted sql

-- changeset elena_zh:1
CREATE TABLE notification_tasks (
    id          UUID DEFAULT gen_random_uuid(),
    chat_id     BIGINT NOT NULL,
    notification    TEXT,
    alarm_date      TIMESTAMP,

    CONSTRAINT  notification_tasks_pk   PRIMARY KEY (id)
);