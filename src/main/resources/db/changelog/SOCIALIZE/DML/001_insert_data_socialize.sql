-- liquibase formatted sql

-- changeset yourname:insert-users
--INSERT INTO users (id, first_name, last_name, email, password, birthday, gender, profile_image, role, activation_code, is_user_new, enabled)
--VALUES
--  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'John', 'Doe', 'john.doe@example.com', 'hashedpassword', '1990-01-01', 'MALE', 'john_profile.jpg', 'USER', 'code1234', FALSE, TRUE),
--  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'Jane', 'Smith', 'jane.smith@example.com', 'hashedpassword', '1992-02-02', 'FEMALE', 'jane_profile.jpg', 'USER', 'code5678', FALSE, TRUE);
--
---- changeset yourname:insert-tokens
--INSERT INTO token (id, token, token_type, revoked, expired, user_id)
--VALUES
--  (uuid_generate_v4(), 'token1', 'BEARER', FALSE, FALSE, 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'),
--  (uuid_generate_v4(), 'token2', 'BEARER', FALSE, FALSE, 'b1b2b1d4-1f2b-11ed-861d-0242ac120002');
--
---- changeset yourname:insert-posts
--INSERT INTO posts (id, description, created_at, user_id)
--VALUES
--  ('c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'Exploring the mountains', '2023-01-01 10:00:00', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'),
--  ('d0d2d1d4-1f2b-11ed-861d-0242ac120002', 'Sunset at the beach', '2023-01-02 18:00:00', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002');
--
---- changeset yourname:insert-comments
--INSERT INTO comments (id, value, created_at, post_id, user_id)
--VALUES
--  (uuid_generate_v4(), 'Looks amazing!', '2023-01-01 11:00:00', 'c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002'),
--  (uuid_generate_v4(), 'Wish I was there!', '2023-01-02 19:00:00', 'd0d2d1d4-1f2b-11ed-861d-0242ac120002', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002');
--
---- changeset yourname:insert-post-images
--INSERT INTO post_images (post_id, image_filename)
--VALUES
--  ('c0c2c1d4-1f2b-11ed-861d-0242ac120002', 'https://images.unsplash.com/photo-1707343844152-6d33a0bb32c3?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
--  ('d0d2d1d4-1f2b-11ed-861d-0242ac120002', 'https://images.unsplash.com/photo-1682687982423-295485af248a?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D');
--
---- changeset yourname:insert-user-post-likes
--INSERT INTO user_post_likes (user_id, post_id)
--VALUES
--  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'd0d2d1d4-1f2b-11ed-861d-0242ac120002'),
--  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'c0c2c1d4-1f2b-11ed-861d-0242ac120002');
--
---- changeset yourname:insert-user-followers
--INSERT INTO user_followers (user_id, follower_id)
--VALUES
--  ('a0a2a1d4-1f2b-11ed-861d-0242ac120002', 'b1b2b1d4-1f2b-11ed-861d-0242ac120002'), -- User A is followed by User B
--  ('b1b2b1d4-1f2b-11ed-861d-0242ac120002', 'a0a2a1d4-1f2b-11ed-861d-0242ac120002'); -- User B is followed by User A



INSERT INTO users (id, first_name, last_name, email, password, birthday, gender, profile_image, role, activation_code, enabled, lives_in, bio, is_user_new) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'John', 'Doe', 'john.doe@example.com', 'password123', '1990-01-01', 'MALE', 'john.jpg', 'USER', '123456', true, 'New York', 'Just a regular user.', false),
('550e8400-e29b-41d4-a716-446655440001', 'Jane', 'Doe', 'jane.doe@example.com', 'password123', '1992-02-02', 'FEMALE', 'jane.png', 'USER', '123457', true, 'Los Angeles', 'Love to travel.', false),
('550e8400-e29b-41d4-a716-446655440002', 'Alice', 'Smith', 'alice.smith@example.com', 'password123', '1985-03-03', 'FEMALE', 'alice.jpg', 'USER', '123458', true, 'Chicago', 'Admin user.', false),
('550e8400-e29b-41d4-a716-446655440003', 'Bob', 'Brown', 'bob.brown@example.com', 'password123', '1988-04-04', 'MALE', 'bob.jpg', 'USER', '123459', true, 'Miami', 'Love to code.', false);

INSERT INTO posts (id, description, created_at, user_id) VALUES
('660e8400-e29b-41d4-a716-446655440000', 'Hello World!', '2024-05-01 10:00:00', '550e8400-e29b-41d4-a716-446655440000'),
('660e8400-e29b-41d4-a716-446655440001', 'Enjoying the sunshine.', '2024-05-02 11:00:00', '550e8400-e29b-41d4-a716-446655440001'),
('660e8400-e29b-41d4-a716-446655440002', 'Great coding session today.', '2024-05-03 12:00:00', '550e8400-e29b-41d4-a716-446655440002');

-- Insert comments
INSERT INTO comments (id, value, created_at, post_id, user_id, parent_id) VALUES
('770e8400-e29b-41d4-a716-446655440000', 'Nice post!', '2024-05-01 11:00:00', '660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', NULL),
('770e8400-e29b-41d4-a716-446655440001', 'Thank you!', '2024-05-01 11:30:00', '660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', '770e8400-e29b-41d4-a716-446655440000'),
('770e8400-e29b-41d4-a716-446655440002', 'Looks fun!', '2024-05-02 12:00:00', '660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003', NULL);

-- Insert post images
INSERT INTO post_images (post_id, image_filename) VALUES
('660e8400-e29b-41d4-a716-446655440000', 'hello_world.jpeg'),
('660e8400-e29b-41d4-a716-446655440001', 'sunshine.png'),
('660e8400-e29b-41d4-a716-446655440002', 'coding_session.jpeg');

-- Insert likes
INSERT INTO user_post_likes (user_id, post_id) VALUES
('550e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440000'),
('550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440001');

-- Insert followers
INSERT INTO user_followers (user_id, follower_id) VALUES
('550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001'),
('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002');

-- Insert user interests
INSERT INTO user_interests (user_id, interest) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'coding'),
('550e8400-e29b-41d4-a716-446655440001', 'traveling');

-- Insert stories
INSERT INTO stories (id, value, expiration_date, created_at, user_id) VALUES
('990e8400-e29b-41d4-a716-446655440000', 'stories/990e8400-e29b-41d4-a716-446655440000/550e8400-e29b-41d4-a716-446655440000/car.jpg', '2024-06-12 12:00:00', '2024-06-11 09:00:00', '550e8400-e29b-41d4-a716-446655440000'),
('990e8400-e29b-41d4-a716-446655440001', 'stories/990e8400-e29b-41d4-a716-446655440001/550e8400-e29b-41d4-a716-446655440001/holiday.png', '2024-05-03 12:00:00', '2024-05-02 12:00:00', '550e8400-e29b-41d4-a716-446655440001'),
('990e8400-e29b-41d4-a716-446655440002', 'stories/990e8400-e29b-41d4-a716-446655440002/550e8400-e29b-41d4-a716-446655440000/sunset.png', '2024-06-12 12:00:00', '2024-06-11 08:00:00', '550e8400-e29b-41d4-a716-446655440000');


-- Insert story views
INSERT INTO story_views (story_id, user_id) VALUES
('990e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001'),
('990e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003');

-- Insert tokens
INSERT INTO token (id, token, token_type, revoked, expired, user_id) VALUES
('aa0e8400-e29b-41d4-a716-446655440000', 'token1', 'BEARER', false, false, '550e8400-e29b-41d4-a716-446655440000'),
('aa0e8400-e29b-41d4-a716-446655440001', 'token2', 'BEARER', false, true, '550e8400-e29b-41d4-a716-446655440001');