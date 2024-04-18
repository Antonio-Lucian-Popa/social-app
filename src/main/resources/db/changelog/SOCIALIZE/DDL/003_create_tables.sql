CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS user_interests (
    user_id UUID NOT NULL,
    interest VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_interests_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
);

-- Create an index on the foreign key column for better join performance
CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);

ALTER TABLE users ADD COLUMN lives_in VARCHAR(100);

ALTER TABLE users ADD COLUMN bio VARCHAR(100);
