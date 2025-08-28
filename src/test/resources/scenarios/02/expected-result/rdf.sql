CREATE TABLE books (
    id TEXT NOT NULL PRIMARY KEY,
    isFromProjectGutenberg BOOLEAN,
    langCode TEXT,
    isFiction BOOLEAN,
    isPoetry BOOLEAN,
    projectGutenbergId TEXT,
    title TEXT,
    creators TEXT,
    productUrl TEXT,
    textUrl TEXT
);
INSERT INTO books (
    id,
    isFromProjectGutenberg,
    langCode,
    isFiction,
    isPoetry,
    projectGutenbergId,
    title,
    creators,
    productUrl,
    textUrl
) VALUES (
    'UUID 1',
    1,
    'en',
    true,
    false,
    '42029',
    'The Girl Scout''s Triumph; or, Rosanna''s Sacrifice',
    'Galt, Katherine Keene',
    'https://www.gutenberg.org/ebooks/42029',
    'https://www.gutenberg.org/ebooks/42029.txt.utf-8'
);
INSERT INTO books (
    id,
    isFromProjectGutenberg,
    langCode,
    isFiction,
    isPoetry,
    projectGutenbergId,
    title,
    creators,
    productUrl,
    textUrl
) VALUES (
    'UUID 2',
    1,
    'en',
    false,
    false,
    '59328',
    'Modern Copper Smelting
being lectures delivered at Birmingham University, greatly extended and adapted and with and introduction on the history, uses and properties of copper.',
    'Levy, Donald M.',
    'https://www.gutenberg.org/ebooks/59328',
    'https://www.gutenberg.org/ebooks/59328.txt.utf-8'
);
