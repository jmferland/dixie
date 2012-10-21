-- TODO: This NEEDS to get changed, right now it's a straight rip

INSERT INTO category
    (parent_id, place, category) VALUES
    (0, 1, 'Technology'),
    (0, 2, 'World & Business'),
    (0, 3, 'Science'),
    (0, 4, 'Gaming'),
    (0, 5, 'Lifestyle'),
    (0, 6, 'Entertainment'),
    (0, 7, 'Sports'),
    (0, 8, 'Offbeat');

INSERT INTO category
    (parent_id, place, category) VALUES
    (1, 1, 'Apple'),
    (1, 2, 'Design'),
    (1, 3, 'Gadgets'),
    (1, 4, 'Hardware'),
    (1, 5, 'Industry News'),
    (1, 6, 'Linux/Unix'),
    (1, 7, 'Microsoft'),
    (1, 8, 'Mods'),
    (1, 9, 'Programming'),
    (1, 10, 'Security'),
    (1, 11, 'Software');

INSERT INTO category
    (parent_id, place, category) VALUES
    (2, 1, 'Business & Financial'),
    (2, 2, 'World News'),
    (2, 3, 'Political News'),
    (2, 4, 'Political Opinion');

INSERT INTO category
    (parent_id, place, category) VALUES
    (3, 1, 'Environment'),
    (3, 2, 'General Sciences'),
    (3, 3, 'Space');

INSERT INTO category
    (parent_id, place, category) VALUES
    (4, 1, 'Industry News'),
    (4, 2, 'PC Games'),
    (4, 3, 'Playable Web Games'),
    (4, 4, 'Nintendo'),
    (4, 5, 'Playstation'),
    (4, 6, 'Xbox');

INSERT INTO category
    (parent_id, place, category) VALUES
    (5, 1, 'Arts & Culture'),
    (5, 2, 'Autos'),
    (5, 3, 'Educational'),
    (5, 4, 'Food & Drink'),
    (5, 5, 'Health'),
    (5, 6, 'Travel & Places');

INSERT INTO category
    (parent_id, place, category) VALUES
    (6, 1, 'Celebrity'),
    (6, 2, 'Music'),
    (6, 3, 'Movies'),
    (6, 4, 'Television'),
    (6, 5, 'Comics & Animation');

INSERT INTO category
    (parent_id, place, category) VALUES
    (7, 1, 'Baseball'),
    (7, 2, 'Basketball'),
    (7, 3, 'Extreme'),
    (7, 4, 'Football-US/Canada'),
    (7, 5, 'Golf'),
    (7, 6, 'Hockey'),
    (7, 7, 'Motorsport'),
    (7, 8, 'Olympics'),
    (7, 9, 'Soccer'),
    (7, 10, 'Tennis'),
    (7, 11, 'Other Sports');

INSERT INTO category
    (parent_id, place, category) VALUES
    (8, 1, 'Comedy'),
    (8, 2, 'Old Stuff'),
    (8, 3, 'People'),
    (8, 4, 'Pets & Animals');

-- Updates lft and rgt values.
CALL buildCategoryTree;