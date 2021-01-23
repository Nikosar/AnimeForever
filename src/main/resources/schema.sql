CREATE TABLE if not exists anime
(
    id           SERIAL PRIMARY KEY,
    provider_id  BIGINT not null,
    next_episode datetime
);

CREATE TABLE if not exists subscription
(
    id         SERIAL PRIMARY KEY,
    user_id    BIGINT not null,
    anime_id   BIGINT references anime (provider_id),
    channel_id BIGINT not null,
    constraint user_anime unique (user_id, anime_id, channel_id)
);

