use work_db;

INSERT INTO categories(id,name) VALUES
(1,'热血'),
(2,'冒险'),
(3,'奇幻'),
(4,'校园'),
(5,'恋爱'),
(6,'悬疑'),
(7,'科幻'),
(8,'治愈');

INSERT INTO works(
    id,
    title,
    cover_url,
    description,
    release_date,
    status,
    created_time,
    updated_time,
    is_deleted
)
VALUES
(1,'鬼灭之刃',
'https://img.test/demon_slayer.jpg',
'炭治郎加入鬼杀队讨伐恶鬼的故事',
'2019-04-06',
'已完结',
NOW(),
NOW(),
0),

(2,'海贼王',
'https://img.test/one_piece.jpg',
'路飞寻找One Piece成为海贼王',
'1999-10-20',
'连载中',
NOW(),
NOW(),
0),

(3,'进击的巨人',
'https://img.test/aot.jpg',
'人类与巨人的战争',
'2013-04-07',
'已完结',
NOW(),
NOW(),
0),

(4,'你的名字',
'https://img.test/your_name.jpg',
'跨越时空的青春爱情故事',
'2016-08-26',
'已完结',
NOW(),
NOW(),
0),

(5,'CLANNAD',
'https://img.test/clannad.jpg',
'温暖治愈的校园故事',
'2007-10-04',
'已完结',
NOW(),
NOW(),
0),

(6,'命运石之门',
'https://img.test/steins_gate.jpg',
'时间旅行与命运抉择',
'2011-04-06',
'已完结',
NOW(),
NOW(),
0),

(7,'刀剑神域',
'https://img.test/sao.jpg',
'虚拟现实游戏中的生存挑战',
'2012-07-08',
'连载中',
NOW(),
NOW(),
0),

(8,'Re:从零开始的异世界生活',
'https://img.test/rezero.jpg',
'死亡回归与异世界冒险',
'2016-04-04',
'连载中',
NOW(),
NOW(),
0),

(9,'紫罗兰永恒花园',
'https://img.test/violet.jpg',
'寻找爱为何物的少女故事',
'2018-01-11',
'已完结',
NOW(),
NOW(),
0),

(10,'辉夜大小姐想让我告白',
'https://img.test/kaguya.jpg',
'天才之间的恋爱头脑战',
'2019-01-12',
'连载中',
NOW(),
NOW(),
0);

INSERT INTO work_category(work_id,category_id) VALUES

-- 鬼灭之刃
(1,1),
(1,2),
(1,3),

-- 海贼王
(2,1),
(2,2),

-- 进击的巨人
(3,1),
(3,6),

-- 你的名字
(4,5),
(4,8),

-- CLANNAD
(5,4),
(5,5),
(5,8),

-- 命运石之门
(6,6),
(6,7),

-- SAO
(7,2),
(7,3),
(7,7),

-- Re0
(8,2),
(8,3),

-- 紫罗兰永恒花园
(9,5),
(9,8),

-- 辉夜大小姐
(10,4),
(10,5);

use character_db;

INSERT INTO characters(
    id,
    name,
    gender,
    description,
    avatar_url,
    created_time,
    is_deleted
)
VALUES
(1,'竈门炭治郎','男',
'鬼灭之刃男主角，善良坚强',
'https://img.test/tanjiro.jpg',
NOW(),0),

(2,'竈门祢豆子','女',
'炭治郎的妹妹',
'https://img.test/nezuko.jpg',
NOW(),0),

(3,'我妻善逸','男',
'鬼杀队成员',
'https://img.test/zenitsu.jpg',
NOW(),0),

(4,'蒙奇·D·路飞','男',
'草帽海贼团船长',
'https://img.test/luffy.jpg',
NOW(),0),

(5,'罗罗诺亚·索隆','男',
'草帽海贼团剑士',
'https://img.test/zoro.jpg',
NOW(),0),

(6,'艾伦·耶格尔','男',
'进击的巨人主角',
'https://img.test/eren.jpg',
NOW(),0),

(7,'三笠·阿克曼','女',
'艾伦的青梅竹马',
'https://img.test/mikasa.jpg',
NOW(),0),

(8,'桐人','男',
'刀剑神域主角',
'https://img.test/kirito.jpg',
NOW(),0),

(9,'亚丝娜','女',
'刀剑神域女主角',
'https://img.test/asuna.jpg',
NOW(),0),

(10,'冈部伦太郎','男',
'命运石之门主角',
'https://img.test/okabe.jpg',
NOW(),0);

INSERT INTO character_relation(
    id,
    source_id,
    target_id,
    relation_type
)
VALUES

(1,1,2,'兄妹'),

(2,1,3,'伙伴'),

(3,4,5,'伙伴'),

(4,6,7,'青梅竹马'),

(5,8,9,'恋人'),

(6,9,8,'恋人'),

(7,2,1,'兄妹'),

(8,7,6,'青梅竹马');

INSERT INTO work_character_actor(
    work_id,
    character_id,
    person_id,
    role_type
)
VALUES

(1,1,1,'声优'),
(1,2,2,'声优'),
(1,3,3,'声优'),

(2,4,4,'声优'),
(2,5,5,'声优'),

(3,6,6,'声优'),
(3,7,7,'声优'),

(7,8,8,'声优'),
(7,9,9,'声优'),

(6,10,10,'声优');

use person_db;
INSERT INTO persons(
    id,
    name,
    country,
    birthday,
    avatar_url,
    introduction,
    created_time,
    is_deleted
)
VALUES

(1,'花江夏树',
'日本',
'1991-06-26',
'https://img.test/hanae.jpg',
'鬼灭之刃中竈门炭治郎的声优',
NOW(),0),

(2,'鬼头明里',
'日本',
'1994-10-16',
'https://img.test/kito.jpg',
'鬼灭之刃中祢豆子的声优',
NOW(),0),

(3,'下野纮',
'日本',
'1980-04-21',
'https://img.test/shimono.jpg',
'鬼灭之刃中善逸的声优',
NOW(),0),

(4,'田中真弓',
'日本',
'1955-01-15',
'https://img.test/tanaka.jpg',
'海贼王中路飞的声优',
NOW(),0),

(5,'中井和哉',
'日本',
'1967-11-25',
'https://img.test/nakai.jpg',
'海贼王中索隆的声优',
NOW(),0),

(6,'梶裕贵',
'日本',
'1985-09-03',
'https://img.test/kaji.jpg',
'进击的巨人中艾伦的声优',
NOW(),0),

(7,'石川由依',
'日本',
'1989-05-30',
'https://img.test/ishikawa.jpg',
'进击的巨人中三笠的声优',
NOW(),0),

(8,'松冈祯丞',
'日本',
'1986-09-17',
'https://img.test/matsuoka.jpg',
'刀剑神域中桐人的声优',
NOW(),0),

(9,'户松遥',
'日本',
'1990-02-04',
'https://img.test/tomatsu.jpg',
'刀剑神域中亚丝娜的声优',
NOW(),0),

(10,'宫野真守',
'日本',
'1983-06-08',
'https://img.test/miyano.jpg',
'命运石之门中冈部伦太郎的声优',
NOW(),0),

(11,'吾峠呼世晴',
'日本',
'1989-05-05',
'https://img.test/gotouge.jpg',
'鬼灭之刃原作者',
NOW(),0),

(12,'尾田荣一郎',
'日本',
'1975-01-01',
'https://img.test/oda.jpg',
'海贼王原作者',
NOW(),0),

(13,'谏山创',
'日本',
'1986-08-29',
'https://img.test/isayama.jpg',
'进击的巨人原作者',
NOW(),0),

(14,'新海诚',
'日本',
'1973-02-09',
'https://img.test/shinkai.jpg',
'你的名字导演兼编剧',
NOW(),0),

(15,'麻枝准',
'日本',
'1975-01-03',
'https://img.test/maeda.jpg',
'CLANNAD原作及剧本作者',
NOW(),0);

INSERT INTO person_work(
    person_id,
    work_id,
    role_type
)
VALUES

(11,1,'作者'),

(12,2,'作者'),

(13,3,'作者'),

(14,4,'导演'),
(14,4,'编剧'),

(15,5,'作者'),

(14,9,'导演');