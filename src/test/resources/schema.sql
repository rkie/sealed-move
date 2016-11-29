create table users(
      username varchar_ignorecase(50) not null primary key,
      password varchar_ignorecase(60) not null,
      enabled boolean not null,
      email varchar_ignorecase(100),
      first_name varchar(50));

create table authorities (
  	  id int not null,
      username varchar_ignorecase(50) not null,
      authority varchar_ignorecase(50) not null,
      constraint fk_authorities_users foreign key(username) references users(username));
      create unique index ix_auth_username on authorities (username,authority);