-- Place (Yer) Verisi
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '101', 30);
INSERT INTO place (building, floor, room, seat) VALUES ('A Blok', '1', '102', 25);
INSERT INTO place (building, floor, room, seat) VALUES ('B Blok', '2', '201', 35);
INSERT INTO place (building, floor, room, seat) VALUES ('C Blok', '3', '301', 20);

-- Student (Öğrenci) Verisi
INSERT INTO student (name, department) VALUES ('Ahmet Yılmaz', 'Bilgisayar Mühendisliği');
INSERT INTO student (name, department) VALUES ('Fatih Demir', 'İnşaat Mühendisliği');
INSERT INTO student (name, department) VALUES ('Zeynep Kaya', 'Elektrik Mühendisliği');
INSERT INTO student (name, department) VALUES ('Ayşe Öz', 'Bilgisayar Mühendisliği');

-- Reservation (Rezervasyon) Verisi
INSERT INTO reservation (date, duration, is_reserved, student_id, place_id) VALUES ('2026-03-10 10:00:00', '2026-03-10 11:00:00', true, 1, 1);
INSERT INTO reservation (date, duration, is_reserved, student_id, place_id) VALUES ('2026-03-10 14:00:00', '2026-03-10 16:00:00', true, 2, 2);
INSERT INTO reservation (date, duration, is_reserved, student_id, place_id) VALUES ('2026-03-11 09:00:00', '2026-03-11 10:00:00', false, 3, 3);
