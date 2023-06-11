CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_id IDENTITY PRIMARY KEY,
    name   varchar(20)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id     IDENTITY PRIMARY KEY,
    name        varchar(32),
    description varchar,
    releaseDate date,
    duration    integer,
    mpa_id      integer,
    FOREIGN KEY (mpa_id) REFERENCES mpa_ratings (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   IDENTITY PRIMARY KEY,
    genre_name varchar(15)
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  integer,
    genre_id integer,
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  IDENTITY PRIMARY KEY,
    email    varchar(50),
    login    varchar(32),
    name     varchar(20),
    birthday date
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id integer,
    user_id integer,
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)

);

CREATE TABLE IF NOT EXISTS user_friends
(
    user_id   integer,
    friend_id integer,
    status    boolean,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id)
);

DELETE FROM user_friends;
DELETE FROM film_genres;
DELETE FROM film_likes;
DELETE FROM films;
DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;


MERGE INTO mpa_ratings
    KEY (mpa_id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres
    KEY (genre_id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');