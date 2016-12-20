INSERT INTO users 
values (
	'bob',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'bob@test.com',
	'Robert');
	
INSERT INTO users 
values (
	'dave',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'dave@test.com',
	'David');
	
INSERT INTO users 
values (
	'tony',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'tony@test.com',
	'Anthony');

INSERT INTO authorities
VALUES (1, 'bob', 'ROLE_USER');
INSERT INTO authorities
VALUES (2, 'dave', 'ROLE_USER');
INSERT INTO authorities
VALUES (3, 'tony', 'ROLE_USER');


INSERT INTO game_status VALUES ('SETUP');
INSERT INTO game_status VALUES ('ACTIVE');
INSERT INTO game_status VALUES ('FINISHED');

insert into game_type (name, display_name, min_players, max_players) VALUES ('chess', 'Chess', 2, 2);
insert into game_type (name, display_name, min_players, max_players) VALUES ('draughts', 'Draughts', 2, 2);
insert into game_type (name, display_name, min_players, max_players) VALUES ('snakes', 'Snakes and Ladders', 2, 4);

-- Add a game that can be joined
insert into games
(type_id, owner, status)
VALUES (
	(SELECT id FROM game_type WHERE name = 'chess'),
	'bob',
	'ACTIVE'
);

insert into join_tokens
(gid, token)
SELECT gid, 'UNIQUE_GAME_ENTRY_TOKEN'
FROM games
WHERE owner = 'bob';

INSERT INTO players (gid, username, play_order)
SELECT gid, owner, 1
FROM games
WHERE owner = 'bob';


-- add a fully occupied game
insert into games
(type_id, owner, status)
VALUES (
	(SELECT id FROM game_type WHERE name = 'draughts'),
	'dave',
	'ACTIVE'
);

insert into join_tokens
(gid, token)
SELECT gid, 'UNIQUE_TOKEN_GAME_FULL'
FROM games
WHERE owner = 'dave';

INSERT INTO players (gid, username, play_order)
SELECT gid, owner, 1
FROM games
WHERE owner = 'dave';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'bob', 2
FROM games
WHERE owner = 'dave';

-- add a completed game
INSERT INTO games (type_id, owner, status) 
VALUES (
	(SELECT id FROM game_type WHERE name = 'snakes'),
	'tony',
	'FINISHED'
);

INSERT INTO players (gid, username, play_order)
SELECT gid, owner, 1 FROM games where owner = 'tony';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'bob', 1 FROM games where owner = 'tony';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'dave', 1 FROM games where owner = 'tony';

