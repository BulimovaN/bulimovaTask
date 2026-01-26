insert into users (
    username, email, password, role, create_date, update_date
) values
      ('admin', 'admin@mail.com', '$2a$10$AM78NSL2UZnN3US2JT7PPO6YCUl0Tkj4dflVodDR83hPc4hCbcaKy', 'ROLE_ADMIN', now(), now()),
      ('user1', 'user1@mail.com', '$2a$10$AM78NSL2UZnN3US2JT7PPO6YCUl0Tkj4dflVodDR83hPc4hCbcaKy', 'ROLE_USER', now(), now()),
      ('user2', 'user2@mail.com', '$2a$10$AM78NSL2UZnN3US2JT7PPO6YCUl0Tkj4dflVodDR83hPc4hCbcaKy', 'ROLE_USER', now(), now());