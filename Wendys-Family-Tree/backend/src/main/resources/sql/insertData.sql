-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse WHERE id < 0;
DELETE FROM owner WHERE id < 0;

-- Owners (10)
INSERT INTO owner (id, first_name, last_name, email) VALUES
  (-1,  'John',     'Smith',    'john.smith@example.com'),
  (-2,  'Emily',    'Johnson',  'emily.johnson@example.com'),
  (-3,  'Michael',  'Brown',    'michael.brown@example.com'),
  (-4,  'Sarah',    'Davis',    'sarah.davis@example.com'),
  (-5,  'Robert',   'Wilson',   'robert.wilson@example.com'),
  (-6,  'Jennifer', 'Martinez', 'jennifer.martinez@example.com'),
  (-7,  'William',  'Anderson', 'william.anderson@example.com'),
  (-8,  'Lisa',     'Thomas',   'lisa.thomas@example.com'),
  (-9,  'David',    'Jackson',  'david.jackson@example.com'),
  (-10, 'Mary',     'White',    NULL)
;

-- Generation 3: great-grandparents (no parents)
INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id) VALUES
  (-8,  'Rose',    'A gentle great-grandmother',  '1995-06-18', 'FEMALE', -4),
  (-9,  'Duke',    'A noble great-grandfather',   '1994-09-05', 'MALE',   -5),
  (-10, 'Bella',   'A graceful great-grandmother','1996-01-25', 'FEMALE', -6),
  (-11, 'Max',     'A strong great-grandfather',  '1995-04-12', 'MALE',   -7)
;

-- Generation 2: grandparents
INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id, father_id, owner_id) VALUES
  (-4, 'Star',   'A shining grandmother', '2000-05-10', 'FEMALE', -8,   -9,   -1),
  (-5, 'Blaze',  'A fiery grandfather',   '1999-08-22', 'MALE',   -10,  -11,  -2),
  (-6, 'Storm',  'A powerful grandmother','2001-02-14', 'FEMALE', NULL, NULL, -3),
  (-7, 'Apollo', 'A majestic grandfather','2000-11-30', 'MALE',   NULL, NULL, -8)
;

-- Generation 1: parents of Wendy
INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id, father_id, owner_id) VALUES
  (-2, 'Luna',    'A graceful mother', '2005-03-15', 'FEMALE', -4, -5, -9),
  (-3, 'Thunder', 'A powerful father', '2004-07-20', 'MALE',   -6, -7, -10)
;

-- Generation 0: Wendy and two more horses
INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id, father_id, owner_id) VALUES
  (-1,  'Wendy', 'The famous one!',          '2012-12-12', 'FEMALE', -2, -3, -1),
  (-12, 'Comet', 'A swift younger sibling',  '2014-08-05', 'MALE',   -2, -3, NULL),
  (-13, 'Pearl', 'A gentle younger sibling', '2016-11-19', 'FEMALE', -2, -3, -8)
;
