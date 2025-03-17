CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8 ;
USE `paymybuddy` ;

CREATE TABLE IF NOT EXISTS `paymybuddy`.`USER` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `balance` DECIMAL NULL,
  `date_created` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);

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
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `paymybuddy`.`BUDDIES` (
  `user_id` INT NOT NULL,
  `buddy_id` INT NOT NULL,
  INDEX `fk_RELATION_USER_buddy_id` (`buddy_id` ASC) INVISIBLE,
  INDEX `fk_RELATION_USER_user_id` (`user_id` ASC) INVISIBLE,
  PRIMARY KEY (`user_id`, `buddy_id`),
  CONSTRAINT `fk_RELATION_USER_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_RELATION_USER_buddy_id`
    FOREIGN KEY (`buddy_id`)
    REFERENCES `paymybuddy`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
