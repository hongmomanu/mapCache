-- Table: imgfile

-- DROP TABLE imgfile;

CREATE TABLE imgfile
(
  id bigserial NOT NULL,
  layerlevel integer,
  layerid integer,
  username character varying(30),
  minx double precision,
  miny double precision,
  maxx double precision,
  maxy double precision,
  status integer,
  issucess boolean,
  updatetime timestamp without time zone,
  imgfile bytea
)
WITH (
  OIDS=FALSE
);
ALTER TABLE imgfile
  OWNER TO postgres;


-- Table: layerlevel

-- DROP TABLE layerlevel;

CREATE TABLE layerlevel
(
  id serial NOT NULL,
  layerid integer,
  updatetime timestamp without time zone,
  layerlevel integer,
  resolution double precision,
  scale double precision
)
WITH (
  OIDS=FALSE
);
ALTER TABLE layerlevel
  OWNER TO postgres;


-- Table: layertype

-- DROP TABLE layertype;

CREATE TABLE layertype
(
  id serial NOT NULL,
  mapid integer,
  layername character varying(500),
  layerlabel character varying(5000),
  updatetime timestamp without time zone,
  minlevel integer, -- 最小级别
  maxlevel integer -- 最大级别
)
WITH (
  OIDS=FALSE
);
ALTER TABLE layertype
  OWNER TO postgres;
COMMENT ON COLUMN layertype.minlevel IS '最小级别';
COMMENT ON COLUMN layertype.maxlevel IS '最大级别';



-- Table: mapcatche

-- DROP TABLE mapcatche;

CREATE TABLE mapcatche
(
  id bigserial NOT NULL,
  layerlevelid integer,
  x integer,
  y integer,
  ltlon double precision,
  ltlat double precision,
  rblon double precision,
  rblat double precision,
  updatetime timestamp without time zone,
  img bytea,
  issucess boolean, -- 获取图片是否成功
  taskid integer -- 任务id taskid
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mapcatche
  OWNER TO postgres;
COMMENT ON COLUMN mapcatche.issucess IS '获取图片是否成功';
COMMENT ON COLUMN mapcatche.taskid IS '任务id taskid';


-- Table: mapserver

-- DROP TABLE mapserver;

CREATE TABLE mapserver
(
  id serial NOT NULL,
  mapower character varying(30),
  owername character varying(30),
  spatialreference character varying(30),
  projection character varying(30),
  updatetime timestamp without time zone,
  maptype integer -- 0为web地图资源，1为arcgis紧凑数据
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mapserver
  OWNER TO postgres;
COMMENT ON COLUMN mapserver.maptype IS '0为web地图资源，1为arcgis紧凑数据';




-- Table: maptype

-- DROP TABLE maptype;

CREATE TABLE maptype
(
  id serial NOT NULL,
  mapname character varying(500),
  maplabel character varying(500),
  ispublic boolean,
  updatetime timestamp without time zone,
  mapowerid integer -- 地图商外键
)
WITH (
  OIDS=FALSE
);
ALTER TABLE maptype
  OWNER TO postgres;
COMMENT ON COLUMN maptype.mapowerid IS '地图商外键';


-- Table: task

-- DROP TABLE task;

CREATE TABLE task
(
  id serial NOT NULL,
  layerid integer,
  layerlevel integer,
  maxx double precision,
  minx double precision,
  maxy double precision,
  miny double precision,
  username character varying(30),
  state integer,
  bgtm timestamp without time zone,
  edtm timestamp without time zone,
  errormsg character varying(30),
  levelid integer -- 层级id
)
WITH (
  OIDS=FALSE
);
ALTER TABLE task
  OWNER TO postgres;
COMMENT ON COLUMN task.levelid IS '层级id';


-- Table: users

-- DROP TABLE users;

CREATE TABLE users
(
  id serial NOT NULL,
  username character varying(30),
  nickname character varying(30),
  passwd character varying(50),
  usertype integer DEFAULT 1, -- 1,一般用户;...
  logintime timestamp without time zone -- 最新登录时间
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;
COMMENT ON COLUMN users.usertype IS '1,一般用户;
0,系统管理员';
COMMENT ON COLUMN users.logintime IS '最新登录时间';

