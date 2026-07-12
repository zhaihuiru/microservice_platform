-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: work_db
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
-- Table structure for table `categories`
--

CREATE DATABASE IF NOT EXISTS work_db
DEFAULT CHARACTER SET utf8mb4;

USE work_db;

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` (`id`, `name`) VALUES (2,'冒险'),(3,'奇幻'),(5,'恋爱'),(6,'悬疑'),(4,'校园'),(8,'治愈'),(1,'热血'),(7,'科幻');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_category`
--

DROP TABLE IF EXISTS `work_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_category` (
  `work_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`work_id`,`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_category`
--

LOCK TABLES `work_category` WRITE;
/*!40000 ALTER TABLE `work_category` DISABLE KEYS */;
INSERT INTO `work_category` (`work_id`, `category_id`) VALUES (1,1),(1,2),(1,3),(2,1),(2,2),(3,1),(3,6),(4,5),(4,8),(5,4),(5,5),(5,8),(6,6),(6,7),(7,2),(7,3),(7,7),(8,2),(8,3),(9,5),(9,8),(10,4),(10,5);
/*!40000 ALTER TABLE `work_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works`
--

DROP TABLE IF EXISTS `works`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `works` (
  `id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `description` text,
  `release_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works`
--

LOCK TABLES `works` WRITE;
/*!40000 ALTER TABLE `works` DISABLE KEYS */;
INSERT INTO `works` (`id`, `title`, `cover_url`, `description`, `release_date`, `status`, `created_time`, `updated_time`, `is_deleted`) VALUES (1,'鬼灭之刃','1.jpeg','炭治郎加入鬼杀队讨伐恶鬼的故事','2019-04-06','已完结','2026-07-07 17:41:55','2026-07-11 11:14:44',0),(2,'海贼王','2.jpeg','路飞寻找One Piece成为海贼王','1999-10-20','连载中','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(3,'进击的巨人','3.jpeg','人类与巨人的战争','2013-04-07','已完结','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(4,'你的名字','4.jpeg','跨越时空的青春爱情故事','2016-08-26','已完结','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(5,'CLANNAD','5.jpeg','温暖治愈的校园故事','2007-10-04','已完结','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(6,'命运石之门','6.jpeg','时间旅行与命运抉择','2011-04-06','已完结','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(7,'刀剑神域','7.jpeg','虚拟现实游戏中的生存挑战','2012-07-08','连载中','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(8,'Re:从零开始的异世界生活','8.jpeg','死亡回归与异世界冒险','2016-04-04','连载中','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(9,'紫罗兰永恒花园','9.jpeg','寻找爱为何物的少女故事','2018-01-11','已完结','2026-07-07 17:41:55','2026-07-11 11:47:13',0),(10,'辉夜大小姐想让我告白','10.jpeg','天才之间的恋爱头脑战','2019-01-12','连载中','2026-07-07 17:41:55','2026-07-11 11:47:13',0);
/*!40000 ALTER TABLE `works` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'work_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 16:19:45
