# lagom-demo

This project has been generated by the lagom/lagom-scala.g8 template.

## Commands

    curl -H 'Content-Type: application/json' -d '{"key": { "profileId": 12345, "pos": "Amazon" }, "day": "2017-05-14", "totals": { "abcd-efgh-ijkl": { "category": "widgets", "total": 81323.77 }, "oiel-kaek-qokd": { "category": "foos", "total": 183.60 } } }' http://localhost:9000/agg

## TODO

- Dependency Injection
- Migrate in-memory Map store to PersistenceEntity
- How to support multiple services in same app? (Does this work?)
- Json with custom map keys is too cumbersome right now
- Json has to be in companion object(s) right now; lame-o
