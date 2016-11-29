INSERT INTO users 
values (
	'bob',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'bob@test.com',
	'Robert');

INSERT INTO authorities
VALUES (1, 'bob', 'ROLE_USER');


INSERT INTO game_status VALUES ('SETUP');
INSERT INTO game_status VALUES ('ACTIVE');
INSERT INTO game_status VALUES ('FINISHED');

insert into game_type (name, min_players, max_players) VALUES ('Chess', 2, 2);
insert into game_type (name, min_players, max_players) VALUES ('Draughts', 2, 2);
insert into game_type (name, min_players, max_players) VALUES ('Snakes and Ladders', 2, 4);