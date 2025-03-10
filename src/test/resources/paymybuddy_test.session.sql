USE paymybuddy_test;

INSERT INTO user (
        id,
        user_name,
        email,
        password,
        balance,
        date_created
    )
VALUES
    (
        1,
        'rory',
        'rory@gmail.com',
        '$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6',
        1000,
        NOW()
    ),
    (
        2,
        'jimi',
        'jimi@gmail.com',
        '$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6',
        1000,
        NOW()
    ),
    (
        3,
        'stevie',
        'stevie@gmail.com',
        '$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6',
        1000,
        NOW()
    )
    ;

INSERT INTO buddies(user_id,buddy_id)
VALUES (1,2),(1,3);

INSERT INTO transaction(id, amount, sender_id, receiver_id, description, fee, date_created)
VALUES (1, 100.00, 1, 3, "test1", 0.00, NOW()),
(2, 200.00, 2, 1, "test2", 0.00, NOW()),
(3, 100.00, 1, 2, "test3", 0.00, NOW()),
(4, 50.00, 3, 2, "test4", 0.00, NOW())
;
