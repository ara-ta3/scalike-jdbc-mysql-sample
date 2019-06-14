create table uniq_test (
    id              int unsigned not null,
    other_id        int unsigned not null,
    unique (id, other_id)
);
