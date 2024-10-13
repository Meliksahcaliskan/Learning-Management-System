-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: lsm
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `assignemnts`
--

DROP TABLE IF EXISTS `assignemnts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignemnts` (
  `assignment_id` int NOT NULL AUTO_INCREMENT,
  `class_id` int DEFAULT NULL,
  `lesson_id` int DEFAULT NULL,
  `description` text NOT NULL,
  `due_date` date NOT NULL,
  `creation_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`assignment_id`),
  KEY `class_id` (`class_id`),
  KEY `lesson_id` (`lesson_id`),
  CONSTRAINT `assignemnts_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  CONSTRAINT `assignemnts_ibfk_2` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignemnts`
--

LOCK TABLES `assignemnts` WRITE;
/*!40000 ALTER TABLE `assignemnts` DISABLE KEYS */;
INSERT INTO `assignemnts` VALUES (58,12,12,'5. Fasikül 9-15. Sayfalar','2024-09-20','2024-09-12 18:31:41');
/*!40000 ALTER TABLE `assignemnts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `date_a` date DEFAULT NULL,
  `attendance` enum('Katıldı','Katılmadı') DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  PRIMARY KEY (`attendance_id`),
  UNIQUE KEY `student_id` (`student_id`,`date_a`),
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (110,21,'2024-09-09','Katıldı','',NULL),(111,22,'2024-09-09','Katıldı','',NULL),(112,23,'2024-09-09','Katıldı','',NULL),(113,24,'2024-09-09','Katıldı','',NULL),(114,25,'2024-09-09','Katıldı','',NULL),(115,26,'2024-09-09','Katıldı','',NULL),(116,58,'2024-09-09','Katılmadı','Özel ders',NULL),(145,21,'2024-09-10','Katıldı','',NULL),(146,22,'2024-09-10','Katıldı','',NULL),(147,23,'2024-09-10','Katıldı','',NULL),(148,24,'2024-09-10','Katıldı','',NULL),(149,25,'2024-09-10','Katıldı','',NULL),(150,26,'2024-09-10','Katıldı','',NULL),(151,58,'2024-09-10','Katılmadı','Veli bilgi vermedi',NULL);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_lessons`
--

DROP TABLE IF EXISTS `class_lessons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_lessons` (
  `class_lesson_id` int NOT NULL AUTO_INCREMENT,
  `class_id` int DEFAULT NULL,
  `lesson_id` int DEFAULT NULL,
  PRIMARY KEY (`class_lesson_id`),
  KEY `class_id` (`class_id`),
  KEY `lesson_id` (`lesson_id`),
  CONSTRAINT `class_lessons_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  CONSTRAINT `class_lessons_ibfk_2` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_lessons`
--

LOCK TABLES `class_lessons` WRITE;
/*!40000 ALTER TABLE `class_lessons` DISABLE KEYS */;
INSERT INTO `class_lessons` VALUES (1,1,4),(2,1,5),(3,1,1),(4,1,6),(5,1,2),(6,2,4),(7,2,5),(8,2,1),(9,2,6),(10,2,2),(11,3,4),(12,3,5),(13,3,3),(14,3,1),(15,3,2),(16,4,4),(17,4,5),(18,4,3),(19,4,1),(20,4,2),(21,5,4),(22,5,5),(23,5,3),(24,5,1),(25,5,2),(26,6,4),(27,6,5),(28,6,3),(29,6,1),(30,6,2),(31,7,11),(32,7,9),(33,7,7),(34,7,12),(35,7,8),(36,7,10),(37,7,2),(38,8,11),(39,8,9),(40,8,7),(41,8,12),(42,8,8),(43,8,10),(44,8,2),(45,9,11),(46,9,9),(47,9,7),(48,9,12),(49,9,8),(50,9,2),(51,9,10),(52,10,11),(53,10,9),(54,10,7),(55,10,12),(56,10,8),(57,10,2),(58,10,10),(59,11,11),(60,11,15),(61,11,12),(62,11,14),(63,11,13),(64,11,2),(65,11,10),(66,12,15),(67,12,12),(68,12,5),(69,12,14),(70,12,2),(71,12,10),(72,13,11),(73,13,9),(74,13,7),(75,13,12),(76,13,8),(77,13,2),(78,13,10),(79,14,11),(80,14,9),(81,14,7),(82,14,12),(83,14,8),(84,14,2),(85,14,10),(86,15,11),(87,15,15),(88,15,12),(89,15,14),(90,15,13),(91,15,2),(92,15,10);
/*!40000 ALTER TABLE `class_lessons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) NOT NULL,
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,'7-A'),(2,'7-B'),(3,'LGS GEN-A'),(4,'LGS GEN-B'),(5,'LGS GEN-C'),(6,'LGS GEN-D'),(7,'11-MF-A'),(8,'11-MF-B'),(9,'12-MF-A'),(10,'12-MF-B'),(11,'12-TM'),(12,'12-DİL'),(13,'MEZ-MF-A'),(14,'MEZ-MF-B'),(15,'MEZ-TM');
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lessons`
--

DROP TABLE IF EXISTS `lessons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lessons` (
  `lesson_id` int NOT NULL AUTO_INCREMENT,
  `lesson_name` varchar(100) NOT NULL,
  PRIMARY KEY (`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lessons`
--

LOCK TABLES `lessons` WRITE;
/*!40000 ALTER TABLE `lessons` DISABLE KEYS */;
INSERT INTO `lessons` VALUES (1,'Matematik'),(2,'Türkçe'),(3,'İnkılap'),(4,'Fen Bilimleri'),(5,'İngilizce'),(6,'Sosyal Bilgiler'),(7,'Fizik'),(8,'Kimya'),(9,'Biyoloji'),(10,'TYT Matematik'),(11,'AYT Matematik'),(12,'Geometri'),(13,'Edebiyat'),(14,'Tarih'),(15,'Coğrafya');
/*!40000 ALTER TABLE `lessons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remembered_users`
--

DROP TABLE IF EXISTS `remembered_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `remembered_users` (
  `id` int DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `password` (`password`),
  UNIQUE KEY `machine_id` (`ip_address`),
  KEY `id` (`id`),
  CONSTRAINT `remembered_users_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remembered_users`
--

LOCK TABLES `remembered_users` WRITE;
/*!40000 ALTER TABLE `remembered_users` DISABLE KEYS */;
INSERT INTO `remembered_users` VALUES (NULL,'admin','12345','CCF9E4DCB950');
/*!40000 ALTER TABLE `remembered_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Admin'),(2,'Koordinatör'),(3,'Öğretmen');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_assignments`
--

DROP TABLE IF EXISTS `student_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_assignments` (
  `student_assignment_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `assignment_id` int DEFAULT NULL,
  `status` enum('Tamamlandı','Yapılmadı','Geç Teslim') DEFAULT 'Yapılmadı',
  `submission_date` date DEFAULT NULL,
  `comment` text,
  PRIMARY KEY (`student_assignment_id`),
  KEY `student_id` (`student_id`),
  KEY `student_assignments_ibfk_2` (`assignment_id`),
  CONSTRAINT `student_assignments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `student_assignments_ibfk_2` FOREIGN KEY (`assignment_id`) REFERENCES `assignemnts` (`assignment_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=247 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_assignments`
--

LOCK TABLES `student_assignments` WRITE;
/*!40000 ALTER TABLE `student_assignments` DISABLE KEYS */;
INSERT INTO `student_assignments` VALUES (244,33,58,'Tamamlandı','2024-09-20',''),(245,34,58,'Tamamlandı','2024-09-20',''),(246,35,58,'Geç Teslim','2024-09-21','Özel durum');
/*!40000 ALTER TABLE `student_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `name_s` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `tc` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `registration_date` date DEFAULT NULL,
  `parent_name` varchar(255) DEFAULT NULL,
  `parent_phone` varchar(255) DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  KEY `class_id` (`class_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'Kaan','UÇAR','40639548290',NULL,'2024-06-26','Nermin DOĞRUYOL','5337754448',6),(3,'Mehmet Efe','CANSIZ',NULL,NULL,'2024-08-24','Elif CANSIZ','5531236142',6),(4,'Uygar Kaan','SAĞIR','48607282962',NULL,'2024-06-26','Derya TUTAM','5304978537',6),(5,'Kazım Baturalp','EHLİZ','42862489104','2011-10-10','2024-01-30','Gülşah EHLİZ','5345243029',6),(6,'Ulaş','ÖZKAN','42379638620','2008-05-28','2024-06-26','Çiğdem ÖZKAN','5062034157',6),(7,'Mina','KAYA','53674256402','2011-08-30','2024-06-26','Meral KAYA','5062877777',5),(8,'Kaan Esat','ÜNGAN',NULL,NULL,'2024-08-29','Seçkin ÜNGAN','5324946323',5),(9,'Ela Duru','GÜNEŞ',NULL,NULL,'2024-08-24','Reşat GÜNEŞ','5343900634',5),(10,'Ela','ÖNCÜL',NULL,NULL,'2024-08-24','Özgür ÖNCÜL','5322980333',5),(11,'Rana','ŞAFAK','52006818998','2010-10-17','2024-06-26','Rahşan ŞAFAK','5058407538',5),(12,'Rüzgar','TAYYAROĞULLARI',NULL,NULL,'2024-08-07','Cemile TAYYAROĞULLARI','5336608154',5),(13,'Ozan','SEVİM','41038532422','2011-06-24','2024-06-26','Eda SEVİM','5332496365',5),(14,'Zeynep Hayrunnisa','BİNGÖL','30793876766','2010-11-10','2024-06-26','Gökçe BİNGÖL','5332182389',4),(15,'Eren','ÖZAY','21401154536','2011-08-21','2024-06-26','Pelin ÖZAY','5332161615',4),(16,'Bora','BİLGE',NULL,NULL,'2024-08-24','Cevat BİLGE','5306409924',4),(17,'Asya Mina','ÜNGAN','15288044912',NULL,'2024-06-26','Musa ÜNGAN','5324946323',4),(18,'Muhammet Emir','ALTUNTAŞ',NULL,NULL,'2024-08-24','Emel ALTUNTAŞ','5305186961',4),(19,'Selin Masal','İŞGÜDER',NULL,NULL,'2024-08-24','Emre İŞGÜDER','5336496944',4),(20,'Mert Ali','BİLGİN',NULL,NULL,'2024-06-26','Özgül BİLGİN','5323159078',4),(21,'Canan Dila','BİLEK','24326742256','2011-08-07','2024-06-26','Yıldız AYHAN BİLEK','5327370210',3),(22,'Eymen Akif','SUBAŞI','69811227006','2011-12-16','2024-06-26','Gülfidan SUBAŞI','5322906135',3),(23,'Azra','PEKKIYICI',NULL,NULL,'2024-08-24','Betül PEKKIYICI','5325659865',3),(24,'Mehmet','KILIÇ','55855041104',NULL,'2024-06-26','Funda KILIÇ','5394019727',3),(25,'Salih Esat','GÜNAYDIN','45772387508','2011-03-13','2024-06-26','Ayşegül GÜNAYDIN','5069259597',3),(26,'Ahmet Emir','AKBEY','55681046960','2011-09-14','2024-06-26','Serpil AKBEY','5303633926',3),(27,'Sude Naz','ÇAVUŞ',NULL,NULL,'2024-08-24','Ümmühan ÇAVUŞ','5352068673',11),(28,'Sidem','ALTUNTAŞ',NULL,NULL,'2024-08-28','Çiğdem ALTUNTAŞ','5369890474',11),(29,'Azra','KANTAR',NULL,NULL,'2024-08-28','Emine BULUT','5347216321',11),(30,'Cansu','KARAHAN','13670286452','2007-11-22','2024-06-26','Yasemin KARAHAN','5522167376',11),(31,'Umut Ali','GÜNEŞ','23855107922',NULL,'2024-08-24','Süheyla GÜNEŞ','5338188118',11),(32,'Zeynep Hira','POYRAZ','28399956630',NULL,'2024-08-24','Serap POYRAZ','5537371634',11),(33,'Ecrin Duru','ESER','40063570522','2007-12-10','2024-06-26','Aliye ESER','5428433932',12),(34,'Nehir Naz','AÇIKELLİ','14219425660',NULL,'2024-08-24','Sevil ERDOĞAN AÇIKELLİ','5303812510',12),(35,'Elif','ÖZTÜRK','26828020816','2008-03-27','2024-06-26','Kenan ÖZTÜRK','5497958653',12),(36,'Ali Umut','KOYUN','36490686776','2008-05-11','2024-06-26','Hanife AKAR KOYUN','5322066195',10),(37,'Irmak','ÇELİK','24800076300','2007-06-08','2024-06-26','Dilay ÇELİK','5438574263',10),(38,'Zilan','ÖMGEN','38554620788','2007-07-08','2023-09-21','Eylem ÖMGEN KAYA',NULL,10),(39,'Derin','ERTUNA','13373295744','2007-09-09','2024-06-26','Oya ERTUNA','5065643111',10),(40,'Nehir','ŞAHİNKAYA','11257939142','2007-05-06','2024-06-26','Erkan ŞAHİNKAYA','5079211423',9),(41,'Yusuf','PEHLİVAN','26432022090','2007-09-26','2024-06-26','Adnan PEHLİVAN','5332215697',9),(42,'Naz','KÖKTAŞ','26993003248','2007-11-16','2024-06-26','Naime KÖKTAŞ','5337255977',9),(43,'Yusuf Enes','ERDOĞAN','34162774554','2007-06-26','2024-06-26','Elif ERDOĞAN','5454880119',9),(44,'Begüm','KÜTÜKOĞLU','10190637344',NULL,'2024-08-24','Ayşe KÜTÜKOĞLU','5511262633',15),(45,'İdil Aybüke','GÜRBÜZ','10285195292',NULL,'2024-08-24','Leyla GÜRBÜZ','5052513009',15),(46,'Viyan','KOŞAR',NULL,NULL,'2024-08-24','Ayfer KOŞAR','5384020413',15),(47,'Nesibe Mehlika','İNCEDERE','10243782052',NULL,'2024-08-24','Musa İNCEDERE','5056497266',13),(48,'Erdem Efe','ZENGİN','26039037954',NULL,'2024-08-20','Esra ZENGİN','5352213684',13),(49,'Vera','ALTUN','19268260644','2006-06-10','2023-10-22','Elif ALTUN','5366748292',13),(50,'Ece','BATUM','10942953968','2007-12-27','2023-07-14','Füsun BATUM','5353699991',13),(51,'Mert Ali','ÖLMEZ',NULL,NULL,'2024-08-07','Senem ÖLMEZ','5303484812',13),(58,'mustafa batuhan','değirmenci','55','0001-01-01','0001-01-01','asd','asd',3);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_role` (`role_id`),
  CONSTRAINT `fk_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','12345',1),(3,'koordinatör','123',2),(4,'ogretmen','234',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'lsm'
--

--
-- Dumping routines for database 'lsm'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-13 12:10:00
