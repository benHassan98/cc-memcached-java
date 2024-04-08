# Write Your Own Memcached Server

https://codingchallenges.fyi/challenges/challenge-memcached

## Description

This is a simple implementation of a Memcached server written in java(vanilla).

The server supports the following commands:

- `get`: Get the value corresponding to the provided key
- `set`: Set a key value pair
- `add`: Add a key value pair if not already present
- `append` : Appends data to an existing key after existing data
- `prepend`: Prepends data to an existing key before existing data
- `incr`: Increments the numeric value of an existing key
- `decr`: Decrements the numeric value of an existing key
- `delete`: Deletes an existing key from the server
- `gets`: Get the value corresponding to the provided key with the cas token
- `cas`: used to set the data if it is not updated since last fetch
- `replace`: Replace a key value pair if present


The server also supports the `expTime`, and `noreply` feature.

## Usage

To start the server, first compile the Main.java file:

```bash
javac Main.java
```

then start the server:

```bash
java Main -p 11211 -m 1
```
