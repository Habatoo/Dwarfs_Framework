DELETE FROM activity;
DELETE FROM levels;
DELETE FROM main_roles;
DELETE FROM status;
DELETE FROM sub_roles;
DELETE FROM tags;
DELETE FROM tokens;
DELETE FROM user_main_role;
DELETE FROM user_sub_role;
DELETE FROM usr;

INSERT INTO main_roles(id, main_role_name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MODERATOR'),
(3, 'ROLE_ADMINISTRATOR');

INSERT INTO sub_roles(id, sub_role_name) VALUES
(1, 'COMMON_USER'),
(2, 'SILVER_USER'),
(3, 'GOLD_USER');

INSERT INTO tags(id, tag_name) VALUES
(1, 'JOGGING'),
(2, 'FITNESS'),
(3, 'CROSSFIT');

INSERT INTO status(id, user_status) VALUES
(1, 'COMMON'),
(2, 'READ_ONLY'),
(3, 'NO_ACTIVITY'),
(4, 'BAN'),
(5, 'CLEAR');

INSERT INTO levels(id, level_name) VALUES
(1, 'FIRST_LEVEL'),
(2, 'SECOND_LEVEL'),
(3, 'THIRD_LEVEL'),
(4, 'FOURTH_LEVEL'),
(5, 'FIFTH_LEVEL'),
(6, 'SIXTH_LEVEL'),
(7, 'SEVENTH_LEVEL'),
(8, 'EIGHTH_LEVEL'),
(9, 'NINTH_LEVEL'),
(10, 'TENTH_LEVEL');

INSERT INTO usr(id, user_name, user_email, password, creation_date, activation_email_status) VALUES
(1, 'admin', 'admin@admin.com', '$2a$10$EfVS7r4YFJVUKtoKtipoAuuj.e6z7ed/nEDNGrXB2z6M52d9zmtkW', current_date, true),
(2, 'mod', 'mod@mod.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS', current_date, true),
(3, 'user', 'user@user.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS', current_date, false);

alter sequence usr_id_seq restart with 10;
