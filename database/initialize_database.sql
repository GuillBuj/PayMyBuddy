CREATE DATABASE IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8;
USE `paymybuddy`;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`USER` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE
) ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `paymybuddy`.`USER_ACCOUNT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`USER_ACCOUNT` (
  `USER_id` INT NOT NULL,
  `balance` DECIMAL NULL,
  PRIMARY KEY (`USER_id`),
  INDEX `fk_USER_ACCOUNT_USER_id` (`USER_id` ASC) VISIBLE,
  CONSTRAINT `fk_USER_ACCOUNT_USER1` FOREIGN KEY (`USER_id`) REFERENCES `paymybuddy`.`USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `paymybuddy`.`BANK_ACCOUNT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`BANK_ACCOUNT` (
  `id` INT NOT NULL,
  `iban` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `iban_UNIQUE` (`iban` ASC) VISIBLE
) ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `paymybuddy`.`ACCOUNT_ID`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`ACCOUNT_ID` (
  `id` INT NOT NULL,
  `user_account_id` INT NOT NULL,
  `bank_account_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_USER_ACCOUNT_user_account_id` (`user_account_id` ASC) VISIBLE,
  INDEX `fk_BANK_ACCOUNT_bank_account_id` (`bank_account_id` ASC) INVISIBLE,
  CONSTRAINT `fk_ACCOUNT_ID_USER_ACCOUNT` FOREIGN KEY (`user_account_id`) REFERENCES `paymybuddy`.`USER_ACCOUNT` (`USER_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ACCOUNT_ID_BANK_ACCOUNT` FOREIGN KEY (`bank_account_id`) REFERENCES `paymybuddy`.`BANK_ACCOUNT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `paymybuddy`.`TRANSACTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`TRANSACTION` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL NOT NULL,
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `description` VARCHAR(45) NULL,
  `fee` DECIMAL NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_TRANSACTION_RELATION_ACCOUNT_ID_receiver_id` (`receiver_id` ASC) INVISIBLE,
  INDEX `fk_TRANSACTION_RELATION_ACCOUNT_ID_sender_id` (`sender_id` ASC) VISIBLE,
  CONSTRAINT `fk_RELATION_ACCOUNT_ID_receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `paymybuddy`.`ACCOUNT_ID` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_ACCOUNT_ID_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `paymybuddy`.`ACCOUNT_ID` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `paymybuddy`.`RELATION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`RELATION` (
  `id` INT NOT NULL,
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `accepted` TINYINT NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_sender_receiver_UNIQUE` (`sender_id` ASC, `receiver_id` ASC) INVISIBLE,
  INDEX `fk_RELATION_USER_receiver_id` (`receiver_id` ASC) INVISIBLE,
  INDEX `fk_RELATION_USER_sender_id` (`sender_id` ASC) INVISIBLE,
  CONSTRAINT `fk_RELATION_USER_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `paymybuddy`.`USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_USER_receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `paymybuddy`.`USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB;
SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;