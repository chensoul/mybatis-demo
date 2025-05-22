create sequence IF NOT EXISTS user_seq start with 1 increment by 50;

create table IF NOT EXISTS tb_user (
    id bigint DEFAULT nextval('user_seq') not null,
    name text not null,
    primary key (id)
);
