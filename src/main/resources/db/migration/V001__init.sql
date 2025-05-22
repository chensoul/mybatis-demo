create table `user` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    primary key (`id`)
) AUTO_INCREMENT=1000;

ALTER TABLE `user` ADD CONSTRAINT `uniqueUsersName` UNIQUE (`name`);

