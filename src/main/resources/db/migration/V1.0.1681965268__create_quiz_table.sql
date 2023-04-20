-- insert your migration script here
create table quizzes
(
    id           uuid not null,
    created_at   timestamp(6),
    created_by   varchar(255),
    updated_at   timestamp(6),
    updated_by   varchar(255),
    version      integer,
    is_published boolean,
    title        varchar(255),
    primary key (id)
)
