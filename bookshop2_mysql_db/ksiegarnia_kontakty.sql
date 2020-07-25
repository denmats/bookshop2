-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: ksiegarnia
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `kontakty`
--

DROP TABLE IF EXISTS `kontakty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kontakty` (
  `pesel` varchar(11) NOT NULL,
  `mail` varchar(30) NOT NULL,
  `adres` varchar(60) NOT NULL,
  `tel` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`pesel`),
  UNIQUE KEY `mail` (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kontakty`
--

LOCK TABLES `kontakty` WRITE;
/*!40000 ALTER TABLE `kontakty` DISABLE KEYS */;
INSERT INTO `kontakty` VALUES ('11111111111','a@','W','123'),('222','dsd','dsd',''),('2222','2','2',''),('22222','22','22','2'),('22222222222','b@','W','321'),('2222333344','erer','rer','2323'),('3333','3','3',''),('333332','dd','dd','d'),('33333333','ww','ww',''),('33333333333','c@','L','222'),('44444','4','4','4'),('44444444','www','ww',''),('44444444444','d@','G','333'),('454545','44','44',''),('5454545','45','45','45'),('55545','erere','rer',''),('6666','1','1',''),('66666','6','6','6'),('66666666','ee','eee','322'),('77777','7','7',''),('8888','rr','r','09'),('88888','ll','ll','00'),('99999','we2@','EWEQ','');
/*!40000 ALTER TABLE `kontakty` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-25 15:23:51
