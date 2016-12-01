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

insert into game_type (name, display_name, min_players, max_players) VALUES ('chess', 'Chess', 2, 2);
insert into game_type (name, display_name, min_players, max_players) VALUES ('draughts', 'Draughts', 2, 2);
insert into game_type (name, display_name, min_players, max_players) VALUES ('snakes', 'Snakes and Ladders', 2, 4);