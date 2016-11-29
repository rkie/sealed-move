CREATE SCHEMA `sealed-move` ;

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, TRIGGER, EXECUTE ON `sealed-move`.* TO 'sm-admin'@'%';


-- Spring security configuration - extended to include some key user info

drop table if exists authorities ;
drop table if exists users ;

create table users(
	username varchar(50) NOT NULL primary key,
	password varchar(60) NOT NULL,
	enabled boolean not null,
	email VARCHAR(100),
	first_name VARCHAR(50)
);

create table authorities (
	id INT NOT NULL AUTO_INCREMENT primary key,
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);

create unique index ix_auth_username on authorities (username,authority);

-- Example: granting ROLE_USER authority means joe hasRole("USER")
-- insert into users values('joe', <bcrypt hashed password>, 1, 'joe@bloggs.ie', 'Joe');
-- insert into authorities values(0, 'joe', 'ROLE_USER');



DROP TABLE if exists moves;
DROP TABLE if exists players ;
DROP TABLE if exists games ;
DROP TABLE if exists game_type ;
DROP TABLE if exists game_status;

CREATE TABLE game_status
(
	status VARCHAR(8) PRIMARY KEY
);

INSERT INTO game_status VALUES ('SETUP');
INSERT INTO game_status VALUES ('ACTIVE');
INSERT INTO game_status VALUES ('FINISHED');

CREATE TABLE game_type
(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(20),
	min_players smallint,
	max_players smallint
);

insert into game_type (name, min_players, max_players) VALUES ('Chess', 2, 2);
insert into game_type (name, min_players, max_players) VALUES ('Draughts', 2, 2);
insert into game_type (name, min_players, max_players) VALUES ('Snakes and Ladders', 2, 4);


CREATE TABLE games 
(
	gid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	type_id INT,
	owner VARCHAR(50),
	status VARCHAR(8),
	constraint fk_games_game_type foreign key(type_id) references game_type(id),
	constraint fk_games_users foreign key(owner) references users(username),
	constraint fk_games_status foreign key(status) references game_status(status)
);

CREATE TABLE players 
(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	gid INT,
	username VARCHAR(50),
	play_order SMALLINT NOT NULL,
	constraint fk_players_users foreign key(username) references users(username),
	constraint fk_players_games foreign key(gid) references games(gid)
);

CREATE TABLE moves
(
	mid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pid INT,
	move VARCHAR(100),
	constraint fk_move_players foreign key(pid) references players(id)
);