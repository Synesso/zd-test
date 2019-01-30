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
java -jar <assembly_jar> [data_dir]

   assembly_jar - the result of running `sbt assembly`
   data_dir     - the directory containing the data files organizations.json, tickets.json & users.json
                  if omitted, `.` is assumed.
```

To run from the project root with the supplied test data: 

`java -jar target/scala-2.12/zd-test-assembly-0.1.0-SNAPSHOT.jar src/test/resources/`

## Commands

### Search

Prints elements from the given category where the field partially or fully matches the given term.

`search <category> <field> [term]`

* Category must be one of `{'user', 'ticket', ('org' | 'organisation' | 'organization')}`.
* Field must be a valid field for the category (see `fields`).
* Term is a _case-insensitive_ match on the _prefix_ of _any_ word in value of the field. e.g. `"au"` will match `"Baked Aubergine"`.
  Term may be omitted, in which case it explicitly searches for missing or empty values.

Example:
```
> search user name del
1 result
_id:             57
created_at:      2016-06-10T01:38:38 -10:00
last_login_at:   2014-04-21T08:53:02 -10:00
url:             http://initech.zendesk.com/api/v2/users/57.json
external_id:     cc91aaa0-49af-42aa-9a85-0278fee1a96c
name:            Mari Deleon
alias:           Mr Caitlin
active:          true
verified:        true
shared:          false
locale:          zh-CN
timezone:        Guatemala
email:           caitlindeleon@flotonic.com
phone:           8645-272-680
signature:       Don't Worry Be Happy!
organization:    Noralex - #113
tags:            Blodgett, Nicut, Smock, Finzel
suspended:       false
role:            admin
related tickets: ticket 189eed9f-b44c-49f3-a904-2c482193996a - "A Catastrophe in Singapore"
                 ticket 62a4326f-7114-499f-9adc-a14e99a7ffb4 - "A Drama in Wallis and Futuna Islands"
                 ticket 710bf26b-d65b-4712-95aa-4d123c06e0d7 - "A Nuisance in Costa Rica"
                 ticket 9216c7b3-9a7b-40cb-8f96-56fca79520eb - "A Problem in Marshall Islands"
```

### Fields (f)

Prints the set of available fields per category.

```
> fields

Search User with one of
===========================
* _id
* active
* alias
* created_at
* email
* external_id
* last_login_at
* locale
* name
* organization_id
* phone
* role
* shared
* signature
* suspended
* tags
* timezone
* url
* verified


Search Organisation with one of
===========================
* _id
* created_at
* details
* domain_names
* external_id
* name
* shared_tickets
* tags
* url


Search Ticket with one of
===========================
* _id
* assignee_id
* created_at
* description
* due_at
* external_id
* has_incidents
* organization_id
* priority
* status
* subject
* submitter_id
* tags
* type
* url
* via
```

### Quit (q)

Exits the interactive user loop.

### Help (h)
Prints this help info within the console.

## Notes

### Indexing

Search terms will match on whole words and prefixes, but not on subsequences. 
e.g. `Mary` will be found with `Mary` & `Mar`, but not `ary`.
This is due to space & time requirements for building a trie that caters for subsequence matches.

### Loose data coupling

Initial versions asserted that all linked domain objects existed, forming a closed data-set. However, the supplied test 
data contains missing cross-references (e.g. tickets that declare non-existent organisations). As such, these links are 
not enforced during data load. 

### Domain object field names

The domain objects have fields matching the JSON field names, and default values in order to simplify parsing.

