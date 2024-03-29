DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

CREATE TABLE if not exists users (
  id integer generated by default as identity not null PRIMARY KEY,
  name varchar(20) not null,
  email varchar(20) not null UNIQUE
);

CREATE TABLE if not exists requests (
  id integer generated by default as identity not null PRIMARY KEY,
  description varchar(100) not null,
  requestor_id integer not null REFERENCES users (id) on delete cascade,
  created TIMESTAMP WITHOUT TIME ZONE not null
);

CREATE TABLE if not exists items (
  id integer generated by default as identity not null PRIMARY KEY,
  name varchar(30) not null,
  description varchar(100) not null,
  available boolean not null,
  owner_id integer not null REFERENCES users (id) on delete cascade,
  request_id integer REFERENCES requests (id) on delete cascade
);

CREATE TABLE if not exists comments (
  id integer generated by default as identity not null PRIMARY KEY,
  text varchar(500) not null,
  item_id integer not null REFERENCES items (id) on delete cascade,
  author_id integer not null REFERENCES users (id) on delete cascade,
  created TIMESTAMP WITHOUT TIME ZONE not null
);

CREATE TABLE if not exists bookings (
  id integer generated by default as identity not null PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE not null,
  end_date TIMESTAMP WITHOUT TIME ZONE not null,
  item_id integer not null REFERENCES items (id) on delete cascade,
  booker_id integer not null REFERENCES users (id) on delete cascade,
  status varchar(10) not null
);
