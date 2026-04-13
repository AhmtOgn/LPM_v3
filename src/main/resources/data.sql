-- 1. Place (Yer) Verisi
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '101', 30);
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '102', 25);
INSERT INTO place (building, floor, room, seat) VALUES ('B Blok', '2', '201', 35);
INSERT INTO place (building, floor, room, seat) VALUES ('C Blok', '3', '301', 20);

-- 2. Student (Öğrenci/Kullanıcı) Verisi
-- NOT: Şifrelerin tamamı "123"dür.
-- Sütunlar: name, department, username, password, role, image_url
INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Ahmet Oğan', 'Yazılım Mühendisliği', 'admin', '$2a$10$dCOCiFA6GdBtZxKpcay2LOxtcUJK/MfBeDK.4dR4wL8AtvPk/2jke', 'ADMIN', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Ahmet Yılmaz', 'Bilgisayar Mühendisliği', 'ahmet123', '$2a$10$dCOCiFA6GdBtZxKpcay2LOxtcUJK/MfBeDK.4dR4wL8AtvPk/2jke', 'USER', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Fatih Demir', 'İnşaat Mühendisliği', 'fatih', '$2a$10$dCOCiFA6GdBtZxKpcay2LOxtcUJK/MfBeDK.4dR4wL8AtvPk/2jke', 'USER', NULL);

INSERT INTO student (name, department, username, password, role, imageurl)
VALUES ('Zeynep Kaya', 'Elektrik Mühendisliği', 'zeynep', '$2a$10$dCOCiFA6GdBtZxKpcay2LOxtcUJK/MfBeDK.4dR4wL8AtvPk/2jke', 'USER', NULL);

-- 3. Reservation (Rezervasyon) Verisi
-- student_id ve place_id değerlerinin yukarıdaki ID'lerle eşleştiğinden emin olun.
-- Sütunlar: start_time, end_time, is_cancelled, student_id, place_id

-- 1. GEÇMİŞ REZERVASYON (10 Nisan 2026)
INSERT INTO reservation (start_time, end_time, is_cancelled, student_id, place_id)
VALUES ('2026-04-10 10:00:00', '2026-04-10 12:00:00', false, 1, 1);

-- 2. GELECEK REZERVASYON (14 Nisan 2026)
INSERT INTO reservation (start_time, end_time, is_cancelled, student_id, place_id)
VALUES ('2026-04-14 14:00:00', '2026-04-14 16:00:00', false, 2, 2);

-- 3. BUGÜN/AKTİF REZERVASYON (13 Nisan 2026 - Şu anki saat dilimi için)
INSERT INTO reservation (start_time, end_time, is_cancelled, student_id, place_id)
VALUES ('2026-04-13 09:00:00', '2026-04-13 21:00:00', false, 3, 3);

-- 4. İPTAL EDİLMİŞ REZERVASYON (Koltuk boşa çıkmış olmalı)
INSERT INTO reservation (start_time, end_time, is_cancelled, student_id, place_id)
VALUES ('2026-04-15 10:00:00', '2026-04-15 11:00:00', true, 2, 4);