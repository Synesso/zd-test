[![Travis](https://travis-ci.org/Synesso/zd-test.svg?branch=master)](https://travis-ci.org/Synesso/zd-test)
[![codecov](https://codecov.io/gh/Synesso/zd-test/branch/master/graph/badge.svg)](https://codecov.io/gh/Synesso/zd-test)


# ZDSearch

An interactive console application for searching Zendesk custom data in JSON format.

## Building

Build with [SBT](https://www.scala-sbt.org/).

```
sbt assembly
```

## Usage

```
java -jar <assembly_jar> [data_dir]`

   assembly_jar - the result of running `sbt assembly`
   data_dir     - the directory containing the data files organizations.json, tickets.json & users.json
                  if omitted, `.` is assumed.
```

To run from the project root with the supplied test data: 
`java -jar target/scala-2.12/zd-test-assembly-0.1.0-SNAPSHOT.jar src/test/resources/`

## Commands

Choose from the following commands:

### Search

Prints elements from the given category where the field partially or fully matches the given term.
* Category must be one of `{'user', 'ticket', 'organisation'}`.
* Field must be a valid field for the category (see `fields`).
* Term is a _case-insensitive_ match on the _prefix_ of any word in value of the field.
   Term may be omitted, in which case it explicitly searches for missing or empty values.

`search <category> <field> [term]`

### Fields

Prints the set of available fields per category.

### Quit (q)

Exits the interactive user loop.

### Help (h)
Prints this help info within the console.

## Notes

### Indexing

Search terms will match on whole words and prefixes, but not on subsequences. 
e.g. `Mary` will be found with `Mary` & `Mar`, but not `ary`.
This is due to space (and probably time) requirements for building a trie that caters to subsequence matches.
(My laptop couldn't fit 10,000 users into 16GB memory)

### Loose data coupling

Initial versions asserted that all linked domain objects existed, forming a closed data-set. However, the supplied test 
data contains broken cross-references (e.g. tickets that declare non-existent organisations). As such, these links are 
not enforced during data load. 

### Domain object field names

The domain objects have fields matching the JSON field names, and default values in order to simplify parsing.

