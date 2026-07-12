-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: person_db
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `person_work`
--

CREATE DATABASE IF NOT EXISTS person_db
DEFAULT CHARACTER SET utf8mb4;

USE person_db;

DROP TABLE IF EXISTS `person_work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_work` (
  `person_id` bigint NOT NULL,
  `work_id` bigint NOT NULL,
  `role_type` varchar(50) NOT NULL,
  PRIMARY KEY (`person_id`,`work_id`,`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_work`
--

LOCK TABLES `person_work` WRITE;
/*!40000 ALTER TABLE `person_work` DISABLE KEYS */;
INSERT INTO `person_work` (`person_id`, `work_id`, `role_type`) VALUES (11,1,'作者'),(12,2,'作者'),(13,3,'作者'),(14,4,'导演'),(14,4,'编剧'),(14,9,'导演'),(15,5,'作者');
/*!40000 ALTER TABLE `person_work` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persons`
--

DROP TABLE IF EXISTS `persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persons` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `country` varchar(100) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `introduction` text,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persons`
--

LOCK TABLES `persons` WRITE;
/*!40000 ALTER TABLE `persons` DISABLE KEYS */;
INSERT INTO `persons` (`id`, `name`, `country`, `birthday`, `avatar_url`, `introduction`, `created_time`, `is_deleted`) VALUES (1,'花江夏树','日本','1991-06-26','1.jpeg','鬼灭之刃中竈门炭治郎的声优','2026-07-08 13:57:52',0),(2,'鬼头明里','日本','1994-10-16','2.jpeg','鬼灭之刃中祢豆子的声优','2026-07-08 13:57:52',0),(3,'下野纮','日本','1980-04-21','3.jpeg','鬼灭之刃中善逸的声优','2026-07-08 13:57:52',0),(4,'田中真弓','日本','1955-01-15','4.jpeg','海贼王中路飞的声优','2026-07-08 13:57:52',0),(5,'中井和哉','日本','1967-11-25','5.jpeg','海贼王中索隆的声优','2026-07-08 13:57:52',0),(6,'梶裕贵','日本','1985-09-03','6.jpeg','进击的巨人中艾伦的声优','2026-07-08 13:57:52',0),(7,'石川由依','日本','1989-05-30','7.jpeg','进击的巨人中三笠的声优','2026-07-08 13:57:52',0),(8,'松冈祯丞','日本','1986-09-17','8.jpeg','刀剑神域中桐人的声优','2026-07-08 13:57:52',0),(9,'户松遥','日本','1990-02-04','9.jpeg','刀剑神域中亚丝娜的声优','2026-07-08 13:57:52',0),(10,'宫野真守','日本','1983-06-08','10.jpeg','命运石之门中冈部伦太郎的声优','2026-07-08 13:57:52',0),(11,'吾峠呼世晴','日本','1989-05-05','11.jpeg','鬼灭之刃原作者','2026-07-08 13:57:52',0),(12,'尾田荣一郎','日本','1975-01-01','12.jpeg','海贼王原作者','2026-07-08 13:57:52',0),(13,'谏山创','日本','1986-08-29','13.jpeg','进击的巨人原作者','2026-07-08 13:57:52',0),(14,'新海诚','日本','1973-02-09','14.jpeg','你的名字导演兼编剧','2026-07-08 13:57:52',0),(15,'麻枝准','日本','1975-01-03','15.jpeg','CLANNAD原作及剧本作者','2026-07-08 13:57:52',0);
/*!40000 ALTER TABLE `persons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'person_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 16:19:23
