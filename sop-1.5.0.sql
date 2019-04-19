ALTER TABLE `sop`.`isv_info` ADD COLUMN `sign_type` TINYINT NOT NULL DEFAULT 1 COMMENT '签名类型，1:RSA2,2:MD5' AFTER `status`;

update isv_info set sign_type=2 where secret <> '';