create table users(
      username varchar_ignorecase(50) not null primary key,
      password varchar_ignorecase(60) not null,
      enabled boolean not null,
      email varchar_ignorecase(100),
      first_name varchar(50));

create table authorities (
  	  id int identity not null,
      username varchar_ignorecase(50) not null,
      authority varchar_ignorecase(50) not null,
      constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username,authority);

CREATE TABLE game_status
(
	status VARCHAR(8) not null PRIMARY KEY
);

CREATE TABLE game_type
(
	id INT identity PRIMARY KEY,
	name varchar(20),
	min_players smallint,
	max_players smallint
);

CREATE TABLE games 
(
	gid INT IDENTITY PRIMARY KEY,
	type_id INT,
	owner VARCHAR(50),
	status VARCHAR(8),
	constraint fk_games_game_type foreign key(type_id) references game_type(id),
	constraint fk_games_users foreign key(owner) references users(username),
	constraint fk_games_status foreign key(status) references game_status(status)
);

CREATE TABLE players 
(
	id INT identity PRIMARY KEY,
	gid INT,
	username VARCHAR(50),
	play_order SMALLINT NOT NULL,
	constraint fk_players_users foreign key(username) references users(username),
	constraint fk_players_games foreign key(gid) references games(gid)
);

CREATE TABLE move
(
	mid INT identity PRIMARY KEY,
	pid INT,
	move VARCHAR(100),
	constraint fk_move_players foreign key(pid) references players(id)
);