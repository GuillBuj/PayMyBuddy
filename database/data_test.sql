DELETE FROM `paymybuddy`.`TRANSACTION`;
DELETE FROM `paymybuddy`.`RELATION`;
DELETE FROM `paymybuddy`.`USER`;
ALTER TABLE `paymybuddy`.`TRANSACTION` AUTO_INCREMENT = 1;
ALTER TABLE `paymybuddy`.`USER` AUTO_INCREMENT = 1;
INSERT INTO `paymybuddy`.`USER` (
        `user_name`,
        `email`,
        `password`,
        `balance`,
        `date_created`
    )
VALUES (
        'john_doe',
        'john.doe@example.com',
        '123',
        100.00,
        NOW()
    ),
    (
        'jane_smith',
        'jane.smith@example.com',
        '123',
        150.00,
        NOW()
    ),
    (
        'alex_lee',
        'alex.lee@example.com',
        '123',
        200.00,
        NOW()
    );
INSERT INTO `paymybuddy`.`RELATION` (
        `sender_id`,
        `receiver_id`,
        `accepted`,
        `date_created`
    )
VALUES (1, 2, 0, NOW()),
    -- John a envoyé une demande à Jane, mais la relation n'est pas acceptée
    (2, 3, 1, NOW()),
    -- Jane a accepté la relation avec Alex
    (1, 3, 1, NOW());
-- John a accepté la relation avec Alex
INSERT INTO `paymybuddy`.`TRANSACTION` (
        `amount`,
        `sender_id`,
        `receiver_id`,
        `description`,
        `fee`,
        `date_created`
    )
VALUES (50.00, 2, 5, 'Gift for birthday', 20.00, NOW());
INSERT INTO `paymybuddy`.`TRANSACTION` (
        `amount`,
        `sender_id`,
        `receiver_id`,
        `description`,
        `fee`,
        `date_created`
    )
VALUES (20.00, 1, 3, 'Transfer for lunch', 10.60, NOW());