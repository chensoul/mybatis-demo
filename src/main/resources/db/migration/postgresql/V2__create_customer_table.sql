create sequence IF NOT EXISTS customer_seq start with 1 increment by 50;

create table IF NOT EXISTS  customer (
    id bigint DEFAULT nextval('customer_seq') not null,
    text text not null,
    primary key (id)
);
