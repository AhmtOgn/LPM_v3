-- 1. Place (Yer) Verisi
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '101', 30);
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '102', 25);
INSERT INTO place (building, floor, room, seat) VALUES ('B Blok', '2', '201', 35);
INSERT INTO place (building, floor, room, seat) VALUES ('C Blok', '3', '301', 20);

-- 2. Student (Öğrenci/Kullanıcı) Verisi
-- NOT: Şifrelerin tamamı "123"dür.
-- Sütunlar: name, department, username, password, role, image_url
INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Ahmet Oğan', 'Yazılım Mühendisliği', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHOn2', 'ADMIN', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Ahmet Yılmaz', 'Bilgisayar Mühendisliği', 'ahmet123', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHOn2', 'USER', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Fatih Demir', 'İnşaat Mühendisliği', 'fatih', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHOn2', 'USER', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Zeynep Kaya', 'Elektrik Mühendisliği', 'zeynep', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHOn2', 'USER', NULL);

-- 3. Reservation (Rezervasyon) Verisi
-- student_id ve place_id değerlerinin yukarıdaki ID'lerle eşleştiğinden emin olun.
INSERT INTO reservation (date, duration, is_reserved, student_id, place_id)
VALUES ('2026-04-10 10:00:00', '2026-04-10 12:00:00', true, 1, 1);

INSERT INTO reservation (date, duration, is_reserved, student_id, place_id)
VALUES ('2026-04-10 14:00:00', '2026-04-10 16:00:00', true, 2, 2);

-- Bu kayıt 'is_reserved' false olduğu için 'Available' görünecek
INSERT INTO reservation (date, duration, is_reserved, student_id, place_id)
VALUES ('2026-04-11 09:00:00', '2026-04-11 10:00:00', false, 3, 3);