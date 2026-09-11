# data

Repositories that turn stored records into domain types. Feature and usecase modules never see storage; `:shared` builds the database in its dependency graph.

## State and history

Current state is read straight from its own tables, never derived from events: Home renders every slot of every open card, so that read has to stay a plain query.

`wish_status_change` and `wish_title_change` are append-only and keep every status and title a wish has had, including undone ones. Nothing reads them yet — they are there for a future history view and for analysis.

- Changing a wish's status or title writes the event and updates the current state in one transaction
- Cards have no event table. Creating, closing and relabelling a card touch only the card's own rows
- Add a history table when the sequence of a value will matter later, not for every mutable field

## What a closed card keeps

Closing a card stops its slots from moving, so the marks it ended with are frozen. Everything else in a slot is read from the wish as it is today, including its title.

## SQLDelight

The schema diagram lives next to the `.sq` files, in `src/commonMain/sqldelight/com/rmakiyama/wishline/data/db/AGENTS.md`.

- Prefer a query's mapper overload (`selectAll(::toWish)`) so rows become domain types without the generated row class
- `asFlow()` re-emits when any table the query names changes. Moving a join or a subquery into Kotlin drops tables from that set, and the screen silently stops updating
- Test a flow by collecting across the change, not by reading again afterwards
- `.sq` files compile against the `sqlite-3-18` dialect. Newer syntax fails to parse whatever the device supports
- A partial index cannot reference another table, so a rule spanning tables belongs in Kotlin
- Referential integrity is the database's job. Do not re-check a foreign key in Kotlin
- SQLite enforces foreign keys only when the connection asks. Each `DatabaseDriverFactory` and the test driver turn them on; a new driver has to do the same

## Schema changes

The app is unreleased: change the `.sq` files and recreate the local database. There are no migrations.

## Tests

`androidHostTest` runs against an in-memory `JdbcSqliteDriver` — a much newer SQLite than any device ships.
