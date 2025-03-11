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
        '$2b$12$NPO6GqMCfpmqlIQZCA7K4.QfY0G1uLbtvvHpjwz8NqmtOm1W3a8ke', -- Password123 encoded with  BCrypt
        1000,
        NOW()
    ),
    (
        2,
        'jimi',
        'jimi@gmail.com',
        '$2b$12$NPO6GqMCfpmqlIQZCA7K4.QfY0G1uLbtvvHpjwz8NqmtOm1W3a8ke',
        1000,
        NOW()
    ),
    (
        3,
        'stevie',
        'stevie@gmail.com',
        '$2b$12$NPO6GqMCfpmqlIQZCA7K4.QfY0G1uLbtvvHpjwz8NqmtOm1W3a8ke',
        1000,
        NOW()
    )
    ;

INSERT INTO buddies(user_id,buddy_id)
VALUES (1,2),(1,3);

INSERT INTO transaction(id, amount, sender_id, receiver_id, description, fee, date_created)
VALUES (1, 100.00, 1, 3, "test1", 0.00, NOW() - INTERVAL 5 DAY),
(2, 200.00, 2, 1, "test2", 0.00, NOW() - INTERVAL 4 DAY),
(3, 100.00, 1, 2, "test3", 0.00, NOW() - INTERVAL 3 DAY),
(4, 50.00, 3, 2, "test4", 0.00, NOW() - INTERVAL 2 DAY)
;
