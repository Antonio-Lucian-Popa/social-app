CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Creating the users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    gender VARCHAR(255),
    profile_image VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    activation_code VARCHAR(36),
    enabled BOOLEAN NOT NULL
);

-- changeset yourname:create-token-table
CREATE TABLE token (
    id UUID PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(255) NOT NULL,
    revoked BOOLEAN NOT NULL,
    expired BOOLEAN NOT NULL,
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- rollback DROP TABLE token;


-- Creating the posts table
CREATE TABLE posts (
    id UUID PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Creating the comments table
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    value VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_id UUID,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id)
);

-- Creating the post_images table for the ElementCollection
CREATE TABLE post_images (
    post_id UUID NOT NULL,
    image_filename VARCHAR(255) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- Creating the user_post_likes join table for the ManyToMany relationship
CREATE TABLE user_post_likes (
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- Creating user followers and following relationship tables
CREATE TABLE user_followers (
    user_id UUID NOT NULL,
    follower_id UUID NOT NULL,
    PRIMARY KEY (user_id, follower_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (follower_id) REFERENCES users(id)
);

ALTER TABLE users ALTER COLUMN birthday TYPE TIMESTAMP;
