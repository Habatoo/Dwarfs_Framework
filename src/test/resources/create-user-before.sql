DELETE FROM user_main_role;
DELETE FROM user_sub_role;
DELETE FROM user_tag;
DELETE FROM activity;
DELETE FROM tags;
DELETE FROM levels;
DELETE FROM main_roles;
DELETE FROM status;
DELETE FROM sub_roles;
DELETE FROM tokens;
DELETE FROM usr;

DELETE FROM usr;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO main_roles(main_role_id, main_role_name) VALUES
(1, 'ROLE_ADMINISTRATOR'), (2, 'ROLE_MODERATOR'), (3, 'ROLE_USER');

INSERT INTO sub_roles(sub_role_id, sub_role_name) VALUES
(1, 'COMMON_USER'), (2, 'SILVER_USER'), (3, 'GOLD_USER');

INSERT INTO tags(tag_id, tag_name) VALUES
(1, 'JOGGING'), (2, 'FITNESS'), (3, 'CROSSFIT');

INSERT INTO status(status_id, user_status) VALUES
(1, 'COMMON'), (2, 'READ_ONLY'), (3, 'NO_ACTIVITY'), (4, 'BAN'), (5, 'CLEAR');

INSERT INTO levels(level_id, level_name) VALUES
(1, 'FIRST_LEVEL'), (2, 'SECOND_LEVEL'), (3, 'THIRD_LEVEL'), (4, 'FOURTH_LEVEL'), (5, 'FIFTH_LEVEL'),
(6, 'SIXTH_LEVEL'), (7, 'SEVENTH_LEVEL'), (8, 'EIGHTH_LEVEL'), (9, 'NINTH_LEVEL'), (10, 'TENTH_LEVEL');

INSERT INTO usr(user_id, user_name, user_email, user_password, user_creation_date, user_email_activation_status) VALUES
(1, 'admin', 'admin@admin.com', '$2a$10$EfVS7r4YFJVUKtoKtipoAuuj.e6z7ed/nEDNGrXB2z6M52d9zmtkW', current_date, true),
(2, 'mod', 'mod@mod.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS', current_date, true),
(3, 'user', 'user@user.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS', current_date, false);

INSERT INTO user_main_role VALUES
(1, 1), (1, 2), (1, 3), (2, 2), (2, 3), (3, 3);

INSERT INTO activity(activity_id, activity_title, activity_index, activity_description, activity_creation_date , activity_user_id) VALUES
(1, 'First activity', uuid_generate_v4(), 'First user body activity FIRST', current_date, 1),
(2, 'Second activity', uuid_generate_v4(), 'First user body activity SECOND', current_date, 1),
(3, 'Third activity', uuid_generate_v4(), 'Second user body activity THIRD', current_date, 2),
(4, 'Fourth activity', uuid_generate_v4(), 'Third user body activity FOURTH', current_date, 3);


alter sequence usr_user_id_seq restart with 10;
alter sequence activity_activity_id_seq restart with 10;