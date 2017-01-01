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

INSERT INTO users
values (
	'tom',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'tom@test.com',
	'Thomas');

-- this user will own a game in setup that's reached min players
INSERT INTO users
values (
	'mike',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'mike@test.com',
	'Michael');

--	this guy will not be involved in any games
INSERT INTO users
values (
	'ron',
	'$2a$10$TSuuMxi6iciko3F2TTAWCeBoEP7IMQfODOYyopk6Sbh0VLI//c98S',
	1,
	'ron@test.com',
	'Ronald');

INSERT INTO authorities
VALUES (1, 'bob', 'ROLE_USER');
INSERT INTO authorities
VALUES (2, 'dave', 'ROLE_USER');
INSERT INTO authorities
VALUES (3, 'tony', 'ROLE_USER');
INSERT INTO authorities
VALUES (4, 'tom', 'ROLE_USER');
INSERT INTO authorities
VALUES (5, 'mike', 'ROLE_USER');
INSERT INTO authorities
VALUES (6, 'ron', 'ROLE_USER');


INSERT INTO game_status VALUES ('SETUP');
INSERT INTO game_status VALUES ('READY');
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
	'SETUP'
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
	'READY'
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

-- Game in progress
insert into games (type_id, owner, status)
VALUES (
	(SELECT id FROM game_type WHERE name = 'chess'),
	'tom',
	'ACTIVE'
);

INSERT INTO players (gid, username, play_order)
SELECT gid, owner, 1 FROM games where owner = 'tom';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'bob', 1 FROM games where owner = 'tom';

-- game in setup that has reached min but not max players
-- also acts as a game where the owner has not joined yet!
insert into games (type_id, owner, status)
VALUES (
	(SELECT id FROM game_type WHERE name = 'snakes'),
	'mike',
	'SETUP'
);

INSERT INTO join_tokens (gid, token)
SELECT gid, 'UNIQUE_TOKEN_GAME_MIN_REACHED'
FROM games WHERE owner = 'mike';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'tony', 1 FROM games where owner = 'mike';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'bob', 1 FROM games where owner = 'mike';

INSERT INTO players (gid, username, play_order)
SELECT gid, 'dave', 1 FROM games where owner = 'mike';
