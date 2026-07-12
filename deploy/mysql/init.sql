-- Anime Atlas 微服务平台数据库初始化脚本
-- 根据当前 backend 实体类与 MyBatis-Plus 表映射整理
-- 适用于 MySQL 8.x 的全新数据卷初始化

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS auth_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS work_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS character_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS person_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rating_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS comment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS favorite_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS chat_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =========================================================
-- 1. auth-service
-- =========================================================
USE auth_db;

CREATE TABLE IF NOT EXISTS auth_user (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    bio VARCHAR(255) DEFAULT NULL,
    status INT NOT NULL DEFAULT 0,
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_user_username (username),
    UNIQUE KEY uk_auth_user_email (email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS auth_role (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_role_code (role_code)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS auth_user_role (
                                              id BIGINT NOT NULL AUTO_INCREMENT,
                                              user_id BIGINT NOT NULL,
                                              role_id BIGINT NOT NULL,
                                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_auth_user_role_user (user_id),
    KEY idx_auth_user_role_role (role_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_operation_log (
                                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                                  operator_id BIGINT DEFAULT NULL,
                                                  target_user_id BIGINT DEFAULT NULL,
                                                  operation_type VARCHAR(50) DEFAULT NULL,
    description VARCHAR(255) DEFAULT NULL,
    request_id VARCHAR(100) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_operation_operator (operator_id),
    KEY idx_operation_target_user (target_user_id),
    KEY idx_operation_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ADMIN / USER 角色和 admin 用户由 auth-service 的 DataInitializer 自动创建。

-- =========================================================
-- 2. work-service
-- =========================================================
USE work_db;

CREATE TABLE IF NOT EXISTS works (
                                     id BIGINT NOT NULL,
                                     title VARCHAR(255) NOT NULL,
    cover_url VARCHAR(500) DEFAULT NULL,
    description TEXT,
    release_date DATE DEFAULT NULL,
    status VARCHAR(50) DEFAULT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_works_title (title),
    KEY idx_works_status (status),
    KEY idx_works_deleted (is_deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT NOT NULL,
                                          name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_name (name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS work_category (
                                             work_id BIGINT NOT NULL,
                                             category_id BIGINT NOT NULL,
                                             PRIMARY KEY (work_id, category_id),
    KEY idx_work_category_category (category_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO categories (id, name) VALUES
(1, '热血'),
(2, '冒险'),
(3, '奇幻'),
(4, '校园'),
(5, '恋爱'),
(6, '悬疑'),
(7, '科幻'),
(8, '治愈');

INSERT IGNORE INTO works
(id, title, cover_url, description, release_date, status, created_time, updated_time, is_deleted)
VALUES
(1, '鬼灭之刃', '1.jpeg', '炭治郎加入鬼杀队讨伐恶鬼的故事', '2019-04-06', '已完结', NOW(), NOW(), 0),
(2, '海贼王', '2.jpeg', '路飞寻找One Piece成为海贼王', '1999-10-20', '连载中', NOW(), NOW(), 0),
(3, '进击的巨人', '3.jpeg', '人类与巨人的战争', '2013-04-07', '已完结', NOW(), NOW(), 0),
(4, '你的名字', '4.jpeg', '跨越时空的青春爱情故事', '2016-08-26', '已完结', NOW(), NOW(), 0),
(5, 'CLANNAD', '5.jpeg', '温暖治愈的校园故事', '2007-10-04', '已完结', NOW(), NOW(), 0),
(6, '命运石之门', '6.jpeg', '时间旅行与命运抉择', '2011-04-06', '已完结', NOW(), NOW(), 0),
(7, '刀剑神域', '7.jpeg', '虚拟现实游戏中的生存挑战', '2012-07-08', '连载中', NOW(), NOW(), 0),
(8, 'Re:从零开始的异世界生活', '8.jpeg', '死亡回归与异世界冒险', '2016-04-04', '连载中', NOW(), NOW(), 0),
(9, '紫罗兰永恒花园', '9.jpeg', '寻找爱为何物的少女故事', '2018-01-11', '已完结', NOW(), NOW(), 0),
(10, '辉夜大小姐想让我告白', '10.jpeg', '天才之间的恋爱头脑战', '2019-01-12', '连载中', NOW(), NOW(), 0);

INSERT IGNORE INTO work_category (work_id, category_id) VALUES
(1,1),(1,2),(1,3),
(2,1),(2,2),
(3,1),(3,6),
(4,5),(4,8),
(5,4),(5,5),(5,8),
(6,6),(6,7),
(7,2),(7,3),(7,7),
(8,2),(8,3),
(9,5),(9,8),
(10,4),(10,5);

-- =========================================================
-- 3. character-service
-- =========================================================
USE character_db;

CREATE TABLE IF NOT EXISTS characters (
                                          id BIGINT NOT NULL,
                                          name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) DEFAULT NULL,
    description TEXT,
    avatar_url VARCHAR(500) DEFAULT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_characters_name (name),
    KEY idx_characters_deleted (is_deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS character_relation (
                                                  id BIGINT NOT NULL,
                                                  source_id BIGINT NOT NULL,
                                                  target_id BIGINT NOT NULL,
                                                  relation_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_source_target (source_id, target_id),
    KEY idx_character_relation_target (target_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS work_character_actor (
                                                    work_id BIGINT NOT NULL,
                                                    character_id BIGINT NOT NULL,
                                                    person_id BIGINT NOT NULL,
                                                    role_type VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (work_id, character_id, person_id),
    KEY idx_wca_character (character_id),
    KEY idx_wca_person (person_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO characters
(id, name, gender, description, avatar_url, created_time, is_deleted)
VALUES
(1, '竈门炭治郎', '男', '鬼灭之刃男主角，善良坚强', '1.jpeg', NOW(), 0),
(2, '竈门祢豆子', '女', '炭治郎的妹妹', '2.jpeg', NOW(), 0),
(3, '我妻善逸', '男', '鬼杀队成员', '3.jpeg', NOW(), 0),
(4, '蒙奇·D·路飞', '男', '草帽海贼团船长', '4.jpeg', NOW(), 0),
(5, '罗罗诺亚·索隆', '男', '草帽海贼团剑士', '5.jpeg', NOW(), 0),
(6, '艾伦·耶格尔', '男', '进击的巨人主角', '6.jpeg', NOW(), 0),
(7, '三笠·阿克曼', '女', '艾伦的青梅竹马', '7.jpeg', NOW(), 0),
(8, '桐人', '男', '刀剑神域主角', '8.jpeg', NOW(), 0),
(9, '亚丝娜', '女', '刀剑神域女主角', '9.jpeg', NOW(), 0),
(10, '冈部伦太郎', '男', '命运石之门主角', '10.jpeg', NOW(), 0);

INSERT IGNORE INTO character_relation (id, source_id, target_id, relation_type) VALUES
(1,1,2,'兄妹'),
(2,1,3,'伙伴'),
(3,4,5,'伙伴'),
(4,6,7,'青梅竹马'),
(5,8,9,'恋人'),
(6,9,8,'恋人'),
(7,2,1,'兄妹'),
(8,7,6,'青梅竹马');

INSERT IGNORE INTO work_character_actor (work_id, character_id, person_id, role_type) VALUES
(1,1,1,'声优'),
(1,2,2,'声优'),
(1,3,3,'声优'),
(2,4,4,'声优'),
(2,5,5,'声优'),
(3,6,6,'声优'),
(3,7,7,'声优'),
(6,10,10,'声优'),
(7,8,8,'声优'),
(7,9,9,'声优');

-- =========================================================
-- 4. person-service
-- =========================================================
USE person_db;

CREATE TABLE IF NOT EXISTS persons (
                                       id BIGINT NOT NULL,
                                       name VARCHAR(255) NOT NULL,
    country VARCHAR(100) DEFAULT NULL,
    birthday DATE DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    introduction TEXT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_persons_name (name),
    KEY idx_persons_deleted (is_deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS person_work (
                                           person_id BIGINT NOT NULL,
                                           work_id BIGINT NOT NULL,
                                           role_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (person_id, work_id, role_type),
    KEY idx_person_work_work (work_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO persons
(id, name, country, birthday, avatar_url, introduction, created_time, is_deleted)
VALUES
(1, '花江夏树', '日本', '1991-06-26', '1.jpeg', '鬼灭之刃中竈门炭治郎的声优', NOW(), 0),
(2, '鬼头明里', '日本', '1994-10-16', '2.jpeg', '鬼灭之刃中祢豆子的声优', NOW(), 0),
(3, '下野纮', '日本', '1980-04-21', '3.jpeg', '鬼灭之刃中善逸的声优', NOW(), 0),
(4, '田中真弓', '日本', '1955-01-15', '4.jpeg', '海贼王中路飞的声优', NOW(), 0),
(5, '中井和哉', '日本', '1967-11-25', '5.jpeg', '海贼王中索隆的声优', NOW(), 0),
(6, '梶裕贵', '日本', '1985-09-03', '6.jpeg', '进击的巨人中艾伦的声优', NOW(), 0),
(7, '石川由依', '日本', '1989-05-30', '7.jpeg', '进击的巨人中三笠的声优', NOW(), 0),
(8, '松冈祯丞', '日本', '1986-09-17', '8.jpeg', '刀剑神域中桐人的声优', NOW(), 0),
(9, '户松遥', '日本', '1990-02-04', '9.jpeg', '刀剑神域中亚丝娜的声优', NOW(), 0),
(10, '宫野真守', '日本', '1983-06-08', '10.jpeg', '命运石之门中冈部伦太郎的声优', NOW(), 0),
(11, '吾峠呼世晴', '日本', '1989-05-05', '11.jpeg', '鬼灭之刃原作者', NOW(), 0),
(12, '尾田荣一郎', '日本', '1975-01-01', '12.jpeg', '海贼王原作者', NOW(), 0),
(13, '谏山创', '日本', '1986-08-29', '13.jpeg', '进击的巨人原作者', NOW(), 0),
(14, '新海诚', '日本', '1973-02-09', '14.jpeg', '你的名字导演兼编剧', NOW(), 0),
(15, '麻枝准', '日本', '1975-01-03', '15.jpeg', 'CLANNAD原作及剧本作者', NOW(), 0);

INSERT IGNORE INTO person_work (person_id, work_id, role_type) VALUES
(11,1,'作者'),
(12,2,'作者'),
(13,3,'作者'),
(14,4,'导演'),
(14,4,'编剧'),
(14,9,'导演'),
(15,5,'作者');

-- =========================================================
-- 5. rating-service
-- =========================================================
USE rating_db;

CREATE TABLE IF NOT EXISTS rating_score (
                                            id BIGINT NOT NULL AUTO_INCREMENT,
                                            user_id BIGINT NOT NULL,
                                            work_id BIGINT NOT NULL,
                                            score INT NOT NULL,
                                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (id),
    UNIQUE KEY uk_user_work (user_id, work_id),
    KEY idx_rating_score_work (work_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rating_work_stat (
                                                id BIGINT NOT NULL AUTO_INCREMENT,
                                                work_id BIGINT NOT NULL,
                                                avg_score DECIMAL(3,2) DEFAULT 0.00,
    rating_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_rating_work_stat_work (work_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rating_vote_record (
                                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                                  user_id BIGINT NOT NULL,
                                                  topic_id BIGINT NOT NULL,
                                                  target_id BIGINT NOT NULL,
                                                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                  PRIMARY KEY (id),
    UNIQUE KEY uk_user_topic (user_id, topic_id),
    KEY idx_rating_vote_topic (topic_id),
    KEY idx_rating_vote_target (target_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- 6. comment-service
-- =========================================================
USE comment_db;

CREATE TABLE IF NOT EXISTS comment_comment (
                                               id BIGINT NOT NULL AUTO_INCREMENT,
                                               user_id BIGINT NOT NULL,
                                               work_id BIGINT NOT NULL,
                                               parent_id BIGINT DEFAULT NULL,
                                               content TEXT NOT NULL,
                                               like_count INT DEFAULT 0,
                                               is_sticky TINYINT(1) DEFAULT 0,
    is_essence TINYINT(1) DEFAULT 0,
    status VARCHAR(255) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_comment_work (work_id),
    KEY idx_comment_user (user_id),
    KEY idx_comment_parent (parent_id),
    KEY idx_comment_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comment_like (
                                            id BIGINT NOT NULL AUTO_INCREMENT,
                                            comment_id BIGINT NOT NULL,
                                            user_id BIGINT NOT NULL,
                                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (id),
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    KEY idx_comment_like_user (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- 7. favorite-service
-- =========================================================
USE favorite_db;

CREATE TABLE IF NOT EXISTS fav_folder (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          user_id BIGINT NOT NULL,
                                          folder_name VARCHAR(100) NOT NULL,
    is_public TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_fav_folder_user (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fav_item (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        folder_id BIGINT NOT NULL,
                                        work_id BIGINT NOT NULL,
                                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (id),
    UNIQUE KEY uk_folder_work (folder_id, work_id),
    KEY idx_fav_item_work (work_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- 8. notification-service
-- =========================================================
USE notification_db;

CREATE TABLE IF NOT EXISTS notice_message (
                                              id BIGINT NOT NULL AUTO_INCREMENT,
                                              title VARCHAR(100) NOT NULL,
    content TEXT,
    notice_type VARCHAR(50) DEFAULT 'SYSTEM',
    sender_id BIGINT DEFAULT NULL,
    target_type VARCHAR(50) DEFAULT NULL,
    target_id BIGINT DEFAULT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notice_message_type (notice_type),
    KEY idx_notice_message_target (target_type, target_id),
    KEY idx_notice_message_deleted (deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notice_user_message (
                                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                                   message_id BIGINT NOT NULL,
                                                   receiver_id BIGINT NOT NULL,
                                                   read_status INT DEFAULT 0,
                                                   read_at DATETIME DEFAULT NULL,
                                                   deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_receiver_read (receiver_id, read_status),
    KEY idx_receiver_created (receiver_id, created_at),
    KEY idx_notice_user_message_message (message_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- 9. chat-service
-- =========================================================
USE chat_db;

CREATE TABLE IF NOT EXISTS chat_conversation (
                                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                                 conversation_type VARCHAR(32) NOT NULL,
    title VARCHAR(100) DEFAULT NULL,
    work_id BIGINT DEFAULT NULL,
    owner_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_conversation_type (conversation_type),
    KEY idx_work_id (work_id),
    KEY idx_owner_id (owner_id),
    KEY idx_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_conversation_member (
                                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                                        conversation_id BIGINT NOT NULL,
                                                        user_id BIGINT NOT NULL,
                                                        member_role VARCHAR(32) NOT NULL DEFAULT 'MEMBER',
    muted TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    last_read_message_id BIGINT DEFAULT NULL,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_conversation_user (conversation_id, user_id),
    KEY idx_member_conversation (conversation_id),
    KEY idx_member_user (user_id),
    KEY idx_member_deleted (deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_message (
                                            id BIGINT NOT NULL AUTO_INCREMENT,
                                            conversation_id BIGINT NOT NULL,
                                            sender_id BIGINT NOT NULL,
                                            client_message_id VARCHAR(128) NOT NULL,
    message_type VARCHAR(32) NOT NULL,
    content TEXT,
    media_url VARCHAR(500) DEFAULT NULL,
    reply_to_message_id BIGINT DEFAULT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    review_status VARCHAR(32) NOT NULL DEFAULT 'PASS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_message_idempotent (conversation_id, sender_id, client_message_id),
    KEY idx_message_conversation (conversation_id),
    KEY idx_message_sender (sender_id),
    KEY idx_message_created (created_at),
    KEY idx_message_deleted (deleted)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_message_mention (
                                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                                    message_id BIGINT NOT NULL,
                                                    mentioned_user_id BIGINT NOT NULL,
                                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    PRIMARY KEY (id),
    KEY idx_mention_message (message_id),
    KEY idx_mention_user (mentioned_user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_moderation_log (
                                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                                   operator_id BIGINT NOT NULL,
                                                   conversation_id BIGINT DEFAULT NULL,
                                                   target_user_id BIGINT DEFAULT NULL,
                                                   message_id BIGINT DEFAULT NULL,
                                                   operation_type VARCHAR(64) NOT NULL,
    reason VARCHAR(500) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_log_operator (operator_id),
    KEY idx_log_conversation (conversation_id),
    KEY idx_log_target_user (target_user_id),
    KEY idx_log_message (message_id),
    KEY idx_log_operation (operation_type)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
