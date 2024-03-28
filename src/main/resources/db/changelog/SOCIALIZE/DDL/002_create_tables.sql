CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message VARCHAR(255),
    type VARCHAR(255), -- Assuming the NotificationType enum is stored as text
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    recipient_user_id UUID,
    initiator_user_id UUID,
    post_id UUID, -- Assuming you have a Post entity with a UUID primary key
    FOREIGN KEY (recipient_user_id) REFERENCES users(id), -- Adjust 'users' table name as necessary
    FOREIGN KEY (initiator_user_id) REFERENCES users(id), -- Adjust 'users' table name as necessary
    FOREIGN KEY (post_id) REFERENCES posts(id) -- Assuming you have a 'posts' table with a UUID primary key
);