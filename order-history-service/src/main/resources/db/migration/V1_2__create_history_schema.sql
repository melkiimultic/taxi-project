CREATE TABLE IF NOT EXISTS history
(
    id  bigint not null
        constraint entry_pkey
            primary key ,
    order_id  bigint not null,
    status      varchar(50),
    user_id   bigint not null,
    driver   varchar(50),
    departure    varchar(150),
    arrival     varchar(150),
    date_time TIMESTAMP

);