CREATE SCHEMA `sealed-move` ;

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, TRIGGER, EXECUTE ON `sealed-move`.* TO 'sm-admin'@'%';

DROP TABLE IF EXISTS game;

CREATE TABLE games (
  gid INT NOT NULL AUTO_INCREMENT,
  owner_user_id INT,
  opponent_user_id INT,
PRIMARY KEY (gid)
) COMMENT='Two person game representation';

DROP TABLE IF EXISTS user;

CREATE TABLE users (
	uid INT NOT NULL AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL,
	email VARCHAR(100),
PRIMARY KEY (uid)
)
COMMENT='Storage of user detail info';



