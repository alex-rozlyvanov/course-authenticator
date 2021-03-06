drop table if exists refresh_token cascade;
drop table if exists roles cascade;
drop table if exists users cascade;
drop table if exists users_roles cascade;
create table refresh_token
(
    id          uuid         not null,
    expiry_date timestamp    not null,
    token       varchar(255) not null,
    user_id     uuid,
    primary key (id)
);
create table roles
(
    id    uuid not null,
    title varchar(255),
    primary key (id)
);
create table users
(
    id         uuid not null,
    enabled    boolean,
    first_name varchar(255),
    last_name  varchar(255),
    password   varchar(255),
    username   varchar(255),
    primary key (id)
);
create table users_roles
(
    user_id uuid not null,
    role_id uuid not null
);
alter table refresh_token
    add constraint UK_r4k4edos30bx9neoq81mdvwph unique (token);
alter table roles
    add constraint UK_nodjpaox51kclukt7yi0hf6qf unique (title);
alter table users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);
alter table refresh_token
    add constraint FKjtx87i0jvq2svedphegvdwcuy foreign key (user_id) references users;
alter table users_roles
    add constraint FKj6m8fwv7oqv74fcehir1a9ffy foreign key (role_id) references roles;
alter table users_roles
    add constraint FK2o0jvgh89lemvvo17cbqvdxaa foreign key (user_id) references users;
