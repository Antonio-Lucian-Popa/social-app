-- liquibase formatted sql

-- changeset yourname:insert-users
INSERT INTO users (id, first_name, last_name, email, password, birthday, gender, profile_image, role, activation_code, enabled)
VALUES
  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'John', 'Doe', 'john.doe@example.com', 'hashedpassword', '1990-01-01', 'MALE', 'john_profile.jpg', 'USER', 'code1234', TRUE),
  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'Jane', 'Smith', 'jane.smith@example.com', 'hashedpassword', '1992-02-02', 'FEMALE', 'jane_profile.jpg', 'USER', 'code5678', TRUE);

-- changeset yourname:insert-tokens
INSERT INTO token (id, token, token_type, revoked, expired, user_id)
VALUES
  (1, 'token1', 'BEARER', FALSE, FALSE, 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'),
  (2, 'token2', 'BEARER', FALSE, FALSE, 'b1b2b1d4-1f2b-11ed-861d-0242ac120002');

-- changeset yourname:insert-posts
INSERT INTO posts (id, description, created_at, user_id)
VALUES
  ('c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'Exploring the mountains', '2023-01-01 10:00:00', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'),
  ('d0d2d1d4-1f2b-11ed-861d-0242ac120002', 'Sunset at the beach', '2023-01-02 18:00:00', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002');

-- changeset yourname:insert-comments
INSERT INTO comments (id, value, created_at, post_id, user_id)
VALUES
  (uuid_generate_v4(), 'Looks amazing!', '2023-01-01 11:00:00', 'c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002'),
  (uuid_generate_v4(), 'Wish I was there!', '2023-01-02 19:00:00', 'd0d2d1d4-1f2b-11ed-861d-0242ac120002', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002');

-- changeset yourname:insert-post-images
INSERT INTO post_images (post_id, image_filename)
VALUES
  ('c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'mountains.jpg'),
  ('d0d2d1d4-1f2b-11ed-861d-0242ac120002', 'beach_sunset.jpg');

-- changeset yourname:insert-user-post-likes
INSERT INTO user_post_likes (user_id, post_id)
VALUES
  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'd0d2d1d4-1f2b-11ed-861d-0242ac120002'),
  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'c0c2c1d4-1f2b-11ed-861d-0242ac120002');

-- changeset yourname:insert-user-followers
INSERT INTO user_followers (user_id, follower_id)
VALUES
  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002'), -- User A is followed by User B
  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'); -- User B is followed by User A
