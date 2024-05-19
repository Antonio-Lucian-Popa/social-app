CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message VARCHAR(255),
    type VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    recipient_user_id UUID,
    initiator_user_id UUID,
    post_id UUID,
    FOREIGN KEY (recipient_user_id) REFERENCES users(id),
    FOREIGN KEY (initiator_user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);