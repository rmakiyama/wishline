# domain

Pure Kotlin model. No Android, no SQL, no framework types.

A wish moves across several cards over time, so a card's slots are never removed. That is what lets a closed card still list what it was made of.

## Where an invariant belongs

Decide by what the check needs.

- **Answerable from the entities alone** — a type enforces it. `BingoCardLayout` cannot be built from the wrong number of wishes, from duplicates, or from a wish that is not planned
- **Needs stored state** — the repository checks it. Whether a wish already sits on an open card is one of these: a wish does not know its cards

Never place the same rule in both.

## States

- Model exclusive states as a sealed interface, and let each state carry its own data
- `WishStatus.Done(at)` makes the timestamp impossible to omit. An enum plus a nullable field lets the two drift apart
