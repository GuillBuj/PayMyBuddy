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
        0,
        NOW()
    ),
    (
        2,
        'jimi',
        'jimi@gmail.com',
        '$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6',
        0,
        NOW()
    ),
    (
        3,
        'stevie',
        'stevie@gmail.com',
        '$2a$10$Wj8wft8eK9F3CZyZ.xYZwTw2LGlJKl5Ejq5fnjFupm17Xx5PAkjr6',
        0,
        NOW()
    )
    ;

INSERT INTO buddies(user_id,buddy_id)
VALUES (1,2),(1,3);