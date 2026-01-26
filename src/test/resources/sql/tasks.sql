insert into tasks (
    title,
    description,
    status,
    user_id,
    deadline,
    create_date,
    update_date
)
values
    ('Task 1', 'Desc 1', 'NEW',  1, null, now(), now()),
    ('Task 2', 'Desc 2', 'DONE', 2, now(), now(), now()),
    ('Task 1', 'Desc 1', 'NEW',  2, null, now(), now());