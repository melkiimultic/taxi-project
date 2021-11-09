CREATE TABLE IF NOT EXISTS clients
(
    id  bigint not null
        constraint clients_pkey
            primary key ,
    username    varchar(50)
                    constraint uq
                        unique,
    password    varchar(50),
    firstname   varchar(50),
    lastname    varchar(50),
    phoneNumber varchar(12)

);