-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: paymybuddy
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `buddies`
--

DROP TABLE IF EXISTS `buddies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buddies` (
  `buddy_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`buddy_id`,`user_id`),
  KEY `FKqneoy12obmcmn4h2j8xtphmgv` (`user_id`),
  CONSTRAINT `FKogdylwre4k4a86d69gvd1u84p` FOREIGN KEY (`buddy_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKqneoy12obmcmn4h2j8xtphmgv` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buddies`
--

LOCK TABLES `buddies` WRITE;
/*!40000 ALTER TABLE `buddies` DISABLE KEYS */;
INSERT INTO `buddies` VALUES (2,1),(1,2),(3,2),(7,2),(1,3),(5,3);
/*!40000 ALTER TABLE `buddies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `amount` decimal(38,2) NOT NULL,
  `fee` decimal(38,2) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `receiver_id` int NOT NULL,
  `sender_id` int NOT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKey21a233t8tlwfsbs228q3b2u` (`receiver_id`),
  KEY `FKjpter5yuohdb58gyg6k5nympt` (`sender_id`),
  CONSTRAINT `FKey21a233t8tlwfsbs228q3b2u` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjpter5yuohdb58gyg6k5nympt` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (100.00,0.00,1,1,2,'2025-02-18 10:01:58.307748',NULL),(100.00,0.00,2,1,2,'2025-02-18 10:02:39.207477',NULL),(100.00,0.00,3,1,2,'2025-02-18 10:03:25.123960',NULL),(100.00,0.00,4,1,2,'2025-02-18 10:10:45.642118',NULL),(1.00,0.00,5,3,2,'2025-02-24 15:00:26.429163','bla'),(1.00,0.00,6,3,2,'2025-02-24 15:01:14.119065','bla'),(1.05,0.00,7,3,2,'2025-02-24 15:07:02.596715','bla'),(0.01,0.00,8,5,2,'2025-02-25 07:42:41.012710','b'),(1.00,0.00,9,2,1,'2025-02-25 08:04:07.587865','bla'),(10.00,0.00,10,3,2,'2025-02-25 08:15:17.836478',''),(1.00,0.00,11,7,2,'2025-02-25 09:53:57.236425',''),(2.00,0.00,12,7,2,'2025-02-25 10:12:10.804428',''),(1.00,0.00,13,1,2,'2025-02-25 13:36:06.879215','');
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `balance` decimal(9,2) NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UKlqjrcobrh9jc8wpcar64q1bfh` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2100.00,1,'2025-02-17 12:26:29.920292','tomi@example.com','$2a$10$aAzkd72nCB335IiD5Ct8.eRadEmeZTx8dSL/uSimGthvXyNE8TGta','tomi'),(83.94,2,'2025-02-17 12:26:33.916650','quentin@example.com','$2a$10$EfxWB80QM1EOrwLCv8Qq1OqDcxqBMdxFnaGpmrUMQhGEiPCKNMq7W','quentin'),(13.05,3,'2025-02-19 08:01:38.307182','ben@example.com','$2a$10$tPIQkID3k/ciFfcpRPkMg.4VhNNuiWf8ct02mM/rWkwXMSyHUSyqe','ben'),(0.00,4,'2025-02-19 10:47:54.839079','azerty@gmail.com','$2a$10$yVb/QT/bVFV1AF3NMhvd0.XovcgTzpeDB.AQKyCHdmguCUoF7vzoO','azerty'),(0.01,5,'2025-02-19 10:48:15.453182','aze@gmail.com','$2a$10$TeshFTlo.u1xehctRXly1eS1bR.AF2EvSUxz6cx1f6INI9eZ8.sOu','aze'),(0.00,6,'2025-02-20 12:09:37.086380','trol@gmail.com','$2a$10$y52c8CV9oh25/iIRuhVS/O3nQ9tULzRZGOem7gw07kHZNOCVbDLmm','troll'),(3.00,7,'2025-02-20 12:30:13.152734','www@www.com','$2a$10$jCM.XMPVjKRDmefNEms8tORm.ZZExk5i3IKMZTX3poItWHqjD35eK','www'),(0.00,8,'2025-02-24 10:23:27.682231','ggg@gmail.com','$2a$10$O2EMjOy/I3voZaeKfO8AnOaKcQLrb0Eu34wawal848aGbanHxl0Iy','guillaume'),(0.00,9,'2025-02-24 10:25:03.979596','bla@gmail.fr','$2a$10$ZWVguOkuAJkywYo2.C1/gu2WDFg7NrZGrtgmSXz8kioTQWd0Mi5Ny','bla'),(0.00,10,'2025-02-24 12:12:30.902015','xxx@gmail.com','$2a$10$Md/IcDOowxsKAb4lPt9WU.FUQpCMt/QtvEIA9UOoRdOoKjz/6cCt6','xxx'),(0.00,11,'2025-02-24 14:42:52.824781','rory@gmail.com','$2a$10$2FJpd/.oOnx9gispqpc9Ge390W0dF2iv7t/L3s1Kfyy./FW8Udju.','rory'),(0.00,12,'2025-02-24 14:44:25.834395','jimi@gmail.com','$2a$10$L/.Tm6CtBQXCogY8.Id5C..d/CmTGlzK9Kuz8p4I3AjXg4nnMYLki','jimi');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-25 16:20:56
