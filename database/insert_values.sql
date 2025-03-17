USE `paymybuddy`;

INSERT INTO `USER` (`user_name`, `email`, `password`, `balance`, `date_created`) VALUES
('Alice', 'alice@example.com', 'password123', 100.00, NOW()),
('Bob', 'bob@example.com', 'password456', 50.00, NOW()),
('Charlie', 'charlie@example.com', 'password789', 75.00, NOW());

INSERT INTO `BUDDIES` (`user_id`, `buddy_id`) VALUES
(1, 2),
(1, 3),
(2, 3),
(3, 1);

-- Insertion des transactions
INSERT INTO `TRANSACTION` (`amount`, `sender_id`, `receiver_id`, `description`, `fee`, `date_created`) VALUES
(10.00, 1, 2, 'Restaurant', 0.50, NOW()),
(25.00, 2, 3, 'Voyage', 1.25, NOW()),
(8.00, 3, 1, 'Cinéma', 0.40, NOW());