CREATE DATABASE IF NOT EXISTS `paymybuddy_test` DEFAULT CHARACTER SET utf8mb4;

USE `paymybuddy_test`;

-- -----------------------------------------------------
-- Table USER
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy_test`.`USER` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `balance` DECIMAL(10, 2) NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table TRANSACTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy_test`.`TRANSACTION` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(10, 2) NOT NULL,
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `description` VARCHAR(45) NULL,
  `fee` DECIMAL(10, 2) NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_TRANSACTION_RELATION_ACCOUNT_ID_receiver_id` (`receiver_id` ASC),
  INDEX `fk_TRANSACTION_RELATION_ACCOUNT_ID_sender_id` (`sender_id` ASC),
  CONSTRAINT `fk_RELATION_ACCOUNT_ID_receiver_id` FOREIGN KEY (`receiver_id`) 
    REFERENCES `paymybuddy_test`.`USER` (`id`) 
    ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_ACCOUNT_ID_sender_id` FOREIGN KEY (`sender_id`) 
    REFERENCES `paymybuddy_test`.`USER` (`id`) 
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table BUDDIES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy_test`.`BUDDIES` (
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`sender_id`, `receiver_id`),
  UNIQUE INDEX `idx_sender_receiver_UNIQUE` (`sender_id`, `receiver_id`) INVISIBLE,
  INDEX `fk_RELATION_USER_receiver_id` (`receiver_id` ASC),
  INDEX `fk_RELATION_USER_sender_id` (`sender_id` ASC),
  CONSTRAINT `fk_RELATION_USER_sender_id` FOREIGN KEY (`sender_id`) 
    REFERENCES `paymybuddy_test`.`USER` (`id`) 
    ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_USER_receiver_id` FOREIGN KEY (`receiver_id`) 
    REFERENCES `paymybuddy_test`.`USER` (`id`) 
    ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;

