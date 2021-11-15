CREATE TABLE IF NOT EXISTS history
(
    id  bigint not null
        constraint entry_pkey
            primary key ,
    orderId  bigint not null,
    status      varchar(50),
    userId   bigint not null,
    driver   varchar(50),
    dateTime TIMESTAMP

);