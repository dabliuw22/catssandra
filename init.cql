CREATE KEYSPACE test WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor' : 1
};

USE test;

CREATE TABLE test.products (
    id text PRIMARY KEY,
    name text,
    stock float
);

INSERT INTO test.products(id, name, stock)
VALUES ('d91ae396-42e7-4483-a3ef-e729c486980f', 'product1', 20);