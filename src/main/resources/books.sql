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