CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Creating the stories table
CREATE TABLE stories (
    id UUID PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Creating the story_views join table
CREATE TABLE story_views (
    story_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (story_id, user_id),
    FOREIGN KEY (story_id) REFERENCES stories(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);