-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: progetto_tiw
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `directory`
--

DROP TABLE IF EXISTS `directory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `directory` (
  `directoryid` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `creation_date` date NOT NULL,
  `is_subdirectory` tinyint NOT NULL DEFAULT '0',
  `father_directory` int DEFAULT NULL,
  `user` int NOT NULL,
  PRIMARY KEY (`directoryid`),
  UNIQUE KEY `user_name_unique` (`name`,`user`,`father_directory`),
  KEY `user_idx` (`user`),
  KEY `father_idx` (`father_directory`),
  CONSTRAINT `father` FOREIGN KEY (`father_directory`) REFERENCES `directory` (`directoryid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user` FOREIGN KEY (`user`) REFERENCES `user` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `directory`
--

LOCK TABLES `directory` WRITE;
/*!40000 ALTER TABLE `directory` DISABLE KEYS */;
INSERT INTO `directory` VALUES (1,'polimi','2022-04-10',0,NULL,1),(2,'analisi1','2022-04-10',1,1,1),(3,'programmazione','2022-04-13',0,NULL,1),(4,'java','2022-04-13',1,3,1),(5,'c/c++','2022-04-18',1,3,1),(6,'CartellaUtente','2022-04-23',0,NULL,3),(7,'SottocartellaUtente','2022-04-23',1,6,3),(8,'SottocartellaUtente2','2022-04-23',1,6,3),(9,'fondamentiDiInformatica','2022-05-13',1,1,1),(10,'api','2022-05-13',1,1,1),(11,'sport','2022-05-13',0,NULL,1),(12,'vecchieFoto','2022-05-13',0,NULL,1),(13,'vacanze2021','2022-05-13',1,12,1),(14,'documentiVari','2022-05-13',1,11,1),(15,'gare2021','2022-05-13',1,11,1),(18,'TestDocumento','2022-05-13',0,NULL,8),(19,'TestSottocartella','2022-05-13',1,18,8),(20,'probabilità','2022-05-14',1,1,1),(22,'ingegneriaDelSoftware','2022-06-01',1,1,1),(41,'CartellaProva','2022-06-23',0,NULL,11),(42,'SottocartellaProva','2022-06-23',1,41,11);
/*!40000 ALTER TABLE `directory` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `directory_BEFORE_INSERT` BEFORE INSERT ON `directory` FOR EACH ROW BEGIN
	CALL check_directory(new.is_subdirectory, new.father_directory);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `directory_BEFORE_UPDATE` BEFORE UPDATE ON `directory` FOR EACH ROW BEGIN
	CALL check_directory(new.is_subdirectory, new.father_directory);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `documentid` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `creation_date` date NOT NULL,
  `summary` text NOT NULL,
  `subdirectory` int NOT NULL,
  `type` varchar(20) NOT NULL DEFAULT '.txt',
  PRIMARY KEY (`documentid`),
  UNIQUE KEY `name_subdirectory_unique` (`name`,`subdirectory`),
  KEY `subdirectory_idx` (`subdirectory`),
  CONSTRAINT `subdirectory` FOREIGN KEY (`subdirectory`) REFERENCES `directory` (`directoryid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
INSERT INTO `document` VALUES (1,'formule','2022-04-18','Formule da sapere per l\'esame',2,'.pdf'),(2,'dimostrazioni','2022-04-18','Dimostrazioni da sapere per l\'esame',10,'.pdf'),(3,'DocumentoUtente','2022-01-23','DocumentoUtente',7,'.pdf'),(4,'esercizi','2022-01-13','Esercizi fatti a lezione, più qualche esercizio fatto da me',2,'.pdf'),(13,'domandeDiTeoria','2022-06-02','Domande di teoria chieste agli esami degli anni scorsi',2,'.docx'),(14,'main','2022-06-07','Main class',5,'.cpp'),(22,'slideLezioni','2022-06-22','Tutte le slide di tutte le lezioni del 2019-2020, alcune contengono degli appunti scritti da me durante le lezioni',2,'.pptx'),(23,'SpecificheProgetto2021','2022-06-22','Specifiche ufficiali del progetto del 2021',10,'.pdf'),(24,'DimostrazioniTeoremi','2022-06-22','Tutte le dimostrazioni fatte in classe: Teorema di Rice, Teorema di Kleene, Pumping Lemma, Teorema 1/2 + 1/2 = 1, ...',10,'.pdf'),(25,'TeoriaDellaComplessita\'','2022-06-22','Teoria della complessita\' degli algoritmi. Complessita\' e macchine di Turing. Teoremi di accelerazione lineare. La macchina RAM. Rapporti di complessita\' tra modelli di calcolo diversi',10,'.pdf'),(26,'Esercitazioni','2022-06-22','Tutti gli esercizi fatti ad esercitazione dal prof',10,'.pdf'),(27,'SlideAlgoritmi','2022-06-22','Slide contenenti tutti gli algoritmi fatti: algoritmi di ordinamento, algoritmi sugli alberi e sui grafi.',10,'.pptx'),(28,'SlideLezioni','2022-06-22','Slide di tutte le lezioni del 2019-2020',9,'.pptx'),(29,'eserciziSulleListe','2022-06-22','Tutte le funzioni implementate in classe sulle liste in c',9,'.c'),(30,'DesignPatterns','2022-06-22','Slide lezione sui design patter',22,'.pptx'),(31,'PrincipioDiSostituzioneDiLiskov','2022-06-22','Slide delle lezioni sul principio di sostituzione di liskov (importante: da sapere bene)',22,'.pptx'),(32,'DispenseSuJava','2022-06-22','Dispense tratte dal libro consigliato su java',22,'.pdf'),(33,'Approfondimenti','2022-06-22','Approfondimento sulla riduzione di problemi nell\'ambito della teoria della computabilità',10,'.pdf'),(34,'VariabiliAleatorieDiscrete','2022-06-22','Dispensa sulle variabili aleatorie discrete',20,'.pdf'),(35,'Esercitazioni','2022-06-22','Esercitazioni anno 2020-2021',20,'.pdf'),(36,'TemiEsami2019','2022-06-22','Tutti gli esercizi presenti nei temi d\'esame del 2019',20,'.pdf'),(37,'CertificatoAgonistico','2022-06-22','Certificato agonistico valido fino al 2024',14,'.docx'),(38,'RisultatiMilano','2022-06-22','Risultati della gara del 25/06/2021 svolta a Milano',15,'.pdf'),(39,'RisultatiTorino','2022-06-22','Risultati della gara svolta a Torino il 10/08/2021',15,'.pdf'),(40,'JsonUtils','2022-06-22','Classe che si occupa di leggere tutti i dati provenienti da file di configurazione in Json',4,'.java'),(41,'Pair','2022-06-22','Classe generica che permette di associare due oggetti di qualsiasi classe',4,'.java'),(43,'Main','2022-06-22','Classe principale del programma',4,'.java'),(44,'Controller','2022-06-22','Classe principale del package controller',4,'.java'),(45,'Model','2022-06-22','Classe principale del package model',4,'.java'),(46,'utils','2022-06-22','Libreria di funzioni usate dal programma',5,'.cpp'),(47,'Spiaggia','2022-06-22','Foto della spiaggia alla sera dell\'arrivo ',13,'.png'),(48,'CenaDiGala','2022-06-22','Cena di gala organizzata l\'ultima sera',13,'.png'),(49,'FestaDiscoteca','2022-06-22','Festa in discoteca per il compleanno di una persona di cui non ricordo il nome',13,'.png'),(80,'prova','2022-06-24','abc',2,'.pdf');
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `document_BEFORE_INSERT` BEFORE INSERT ON `document` FOR EACH ROW BEGIN
	CALL check_document(NEW.subdirectory);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `document_BEFORE_UPDATE` BEFORE UPDATE ON `document` FOR EACH ROW BEGIN
	CALL check_document(NEW.subdirectory);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `password` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'CarloSgara','123456','carlo.sgaravatti@gmail.com'),(3,'Utente','12345678','utente@hotmail.com'),(5,'Utente1','123','utente1@icloud.com'),(7,'TestEmail','123456','carlo.sgaravatti@gmail.com'),(8,'TestNoContenuti','ciaociao','testNoContenuti@hotmail.com'),(9,'Prova','123456789','prova@mail.it'),(10,'Prova2','123456789','carlo.sgaravatti@gmail.com'),(11,'Prova3','1234567890','carlo.sgaravatti@gmail.com'),(12,'ProvaRegistrazione','provaprova','prova.registrazione@gmail.com'),(13,'Prova4','123456789','carlo.sgaravatti@gmail.com');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'progetto_tiw'
--

--
-- Dumping routines for database 'progetto_tiw'
--
/*!50003 DROP PROCEDURE IF EXISTS `check_directory` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_directory`(IN is_subdir TINYINT, IN father_dir INT)
BEGIN
	/*this because mysql doesn't have a boolean type*/
    IF is_subdir <> 1 AND is_subdir <> 0 THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'An error occurred';
	END IF;
    
    /*a directory cannot have a father directory*/
	IF is_subdir = 0 AND father_dir IS NOT NULL THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'An error occurred';
	/*a subdirectory father must be specified*/
    ELSEIF is_subdir = 1 AND father_dir IS NULL THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'An error occurred';
	/*a subdirectory father must be a directory and not a subdirectory*/
    ELSEIF is_subdir = 1 AND father_dir = ANY (
		SELECT directoryid FROM progetto_tiw.directory WHERE is_subdirectory = 1
		) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'An error occurred';
	END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `check_document` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_document`(IN subdir INT)
BEGIN
/*a document can be inserted only in a subdirectory*/
	IF subdir = ANY (
		SELECT directoryid FROM progetto_tiw.directory WHERE is_subdirectory = 0
		) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'An error occurred';
	END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-09-16 12:14:38
