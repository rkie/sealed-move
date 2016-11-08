CREATE SCHEMA `sealed-move` ;

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, TRIGGER, EXECUTE ON `sealed-move`.* TO 'sm-admin'@'%';

DROP TABLE IF EXISTS game;

CREATE TABLE games (
  gid INT NOT NULL AUTO_INCREMENT,
  owner_user_id INT,
  opponent_user_id INT,
PRIMARY KEY (gid)
) COMMENT='Two person game representation';

DROP TABLE IF EXISTS players;

CREATE TABLE players (
	pid INT NOT NULL AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL,
	email VARCHAR(100),
PRIMARY KEY (pid)
)
COMMENT='Storage of player detail info';

-- Spring security configuration
-- NOTE I think spring should automatically generate these, but the syntax
-- being used was not correct. May need to tweak a setting.

drop table authorities if exists;
drop table users if exists;

create table users(
	username varchar(50) NOT NULL primary key,
	password varchar(60) NOT NULL,
	enabled boolean not null
);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

-- Example: granting ROLE_USER authority means joe hasRole("USER")
-- insert into users values('joe', <bcrypt hashed password>, 1);
-- insert into authorities values('joe', 'ROLE_USER');



