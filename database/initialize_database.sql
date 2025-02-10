-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8 ;
USE `paymybuddy` ;

-- -----------------------------------------------------
-- Table `paymybuddy`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`USER` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `balance` DECIMAL NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB;


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
  INDEX `fk_TRANSACTION_RELATION_USER_ID_receiver_id` (`receiver_id` ASC) INVISIBLE,
  INDEX `fk_TRANSACTION_RELATION_USER_ID_sender_id` (`sender_id` ASC) INVISIBLE,
  CONSTRAINT `fk_RELATION_USER_ID_receiver_id`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_USER_ID_sender_id`
    FOREIGN KEY (`sender_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddy`.`RELATION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`RELATION` (
  `sender_id` INT NOT NULL,
  `receiver_id` INT NOT NULL,
  `accepted` TINYINT NULL,
  `date_created` DATETIME NULL,
  INDEX `fk_RELATION_USER_receiver_id` (`receiver_id` ASC) INVISIBLE,
  INDEX `fk_RELATION_USER_sender_id` (`sender_id` ASC) INVISIBLE,
  PRIMARY KEY (`sender_id`, `receiver_id`),
  CONSTRAINT `fk_RELATION_USER_sender_id`
    FOREIGN KEY (`sender_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_USER_receiver_id`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
