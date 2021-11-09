CREATE TABLE IF NOT EXISTS orders
(
    id  bigint not null
        constraint order_pkey
            primary key ,
    status      varchar(50),
    userId   bigint not null,
    driver   varchar(50)

);