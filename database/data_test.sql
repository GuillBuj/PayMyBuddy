-- Insérer des utilisateurs
INSERT INTO `paymybuddy`.`USER` (user_name, email, password, date_created) VALUES
('JeanDupont', 'jean.dupont@email.com', 'password123', NOW()),
('MarieMartin', 'marie.martin@email.com', 'password456', NOW()),
('TomiLapipe', 'tomi.lapipe@email.com', 'password789', NOW());

-- Insérer des comptes utilisateurs
INSERT INTO `paymybuddy`.`USER_ACCOUNT` (USER_id, balance) VALUES
(1, 1500.00),
(2, 2300.50),
(3, 200.75);

-- Insérer des comptes bancaires
INSERT INTO `paymybuddy`.`BANK_ACCOUNT` (id, iban) VALUES
(1, 'FR7630004000031234567890143'),
(2, 'FR7630004000039876543210987'),
(3, 'FR7630004000035647382910562');

-- Associer comptes utilisateurs et bancaires
INSERT INTO `paymybuddy`.`ACCOUNT_ID` (id, user_account_id, bank_account_id) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3);

-- Insérer des transactions
INSERT INTO `paymybuddy`.`TRANSACTION` (amount, sender_id, receiver_id, description, fee, date_created) VALUES
(250.00, 1, 2, 'Paiement loyer', 2.50, NOW()),
(100.00, 2, 3, 'Remboursement dîner', 1.00, NOW()),
(75.50, 3, 1, 'Achat en commun', 0.75, NOW());

-- Insérer des relations (amis)
INSERT INTO `paymybuddy`.`RELATION` (id, sender_id, receiver_id, accepted, date_created) VALUES
(1, 1, 2, 1, NOW()),
(2, 1, 3, 1, NOW()),
(3, 2, 3, 0, NOW());
