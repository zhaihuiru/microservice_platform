-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: character_db
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
-- Table structure for table `character_relation`
--

CREATE DATABASE IF NOT EXISTS character_db
DEFAULT CHARACTER SET utf8mb4;

USE character_db;

DROP TABLE IF EXISTS `character_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character_relation` (
  `id` bigint NOT NULL,
  `source_id` bigint NOT NULL,
  `target_id` bigint NOT NULL,
  `relation_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source_target` (`source_id`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_relation`
--

LOCK TABLES `character_relation` WRITE;
/*!40000 ALTER TABLE `character_relation` DISABLE KEYS */;
INSERT INTO `character_relation` (`id`, `source_id`, `target_id`, `relation_type`) VALUES (1,1,2,'兄妹'),(2,1,3,'伙伴'),(3,4,5,'伙伴'),(4,6,7,'青梅竹马'),(5,8,9,'恋人'),(6,9,8,'恋人'),(7,2,1,'兄妹'),(8,7,6,'青梅竹马');
/*!40000 ALTER TABLE `character_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `characters`
--

DROP TABLE IF EXISTS `characters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `characters` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `gender` varchar(20) DEFAULT NULL,
  `description` text,
  `avatar_url` varchar(500) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `characters`
--

LOCK TABLES `characters` WRITE;
/*!40000 ALTER TABLE `characters` DISABLE KEYS */;
INSERT INTO `characters` (`id`, `name`, `gender`, `description`, `avatar_url`, `created_time`, `is_deleted`) VALUES (1,'竈门炭治郎','男','鬼灭之刃男主角，善良坚强','1.jpeg','2026-07-08 13:34:07',0),(2,'竈门祢豆子','女','炭治郎的妹妹','2.jpeg','2026-07-08 13:34:07',0),(3,'我妻善逸','男','鬼杀队成员','3.jpeg','2026-07-08 13:34:07',0),(4,'蒙奇·D·路飞','男','草帽海贼团船长','4.jpeg','2026-07-08 13:34:07',0),(5,'罗罗诺亚·索隆','男','草帽海贼团剑士','5.jpeg','2026-07-08 13:34:07',0),(6,'艾伦·耶格尔','男','进击的巨人主角','6.jpeg','2026-07-08 13:34:07',0),(7,'三笠·阿克曼','女','艾伦的青梅竹马','7.jpeg','2026-07-08 13:34:07',0),(8,'桐人','男','刀剑神域主角','8.jpeg','2026-07-08 13:34:07',0),(9,'亚丝娜','女','刀剑神域女主角','9.jpeg','2026-07-08 13:34:07',0),(10,'冈部伦太郎','男','命运石之门主角','10.jpeg','2026-07-08 13:34:07',0);
/*!40000 ALTER TABLE `characters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_character_actor`
--

DROP TABLE IF EXISTS `work_character_actor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_character_actor` (
  `work_id` bigint NOT NULL,
  `character_id` bigint NOT NULL,
  `person_id` bigint NOT NULL,
  `role_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`work_id`,`character_id`,`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_character_actor`
--

LOCK TABLES `work_character_actor` WRITE;
/*!40000 ALTER TABLE `work_character_actor` DISABLE KEYS */;
INSERT INTO `work_character_actor` (`work_id`, `character_id`, `person_id`, `role_type`) VALUES (1,1,1,'声优'),(1,2,2,'声优'),(1,3,3,'声优'),(2,4,4,'声优'),(2,5,5,'声优'),(3,6,6,'声优'),(3,7,7,'声优'),(6,10,10,'声优'),(7,8,8,'声优'),(7,9,9,'声优');
/*!40000 ALTER TABLE `work_character_actor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'character_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 16:17:47
