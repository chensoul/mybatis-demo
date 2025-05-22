create sequence IF NOT EXISTS user_seq start with 10000 increment by 1;

create table IF NOT EXISTS tb_user (
    id bigint DEFAULT nextval('user_seq') not null,
    name text not null,
    primary key (id)
);

ALTER TABLE "tb_user" ADD CONSTRAINT "uniqueUsersName" UNIQUE ("name");

