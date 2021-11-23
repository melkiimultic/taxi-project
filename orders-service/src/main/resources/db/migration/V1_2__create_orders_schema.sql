CREATE TABLE IF NOT EXISTS orders
(
    id  bigint not null
        constraint order_pkey
            primary key ,
    status      varchar(50),
    user_id   bigint not null,
    driver   varchar(50)

);