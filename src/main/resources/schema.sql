CREATE TABLE if not exists anime
(
    id           SERIAL PRIMARY KEY,
    provider_id  int,
    next_episode datetime
);

CREATE TABLE if not exists subscription
(
    id            SERIAL PRIMARY KEY,
    user_id       int,
    anime_id      int references anime (provider_id),
    is_from_guild boolean,
    channel_id    int,
    constraint user_anime unique (user_id, anime_id, channel_id)
);

