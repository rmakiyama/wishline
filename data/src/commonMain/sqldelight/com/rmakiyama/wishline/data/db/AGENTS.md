# db

**When you change a `.sq` file, update this diagram in the same commit.**

```mermaid
erDiagram
    wish ||--o{ bingo_slot : "sits on, one open card at a time"
    bingo_card ||--|{ bingo_slot : "25 slots"
    wish ||--o{ wish_status_change : "append-only"
    wish ||--o{ wish_title_change : "append-only"

    wish {
        TEXT id PK
        TEXT title
        TEXT status "PLANNED / DONE / SOMEDAY"
        INTEGER status_at
        INTEGER created_at
    }
    bingo_card {
        TEXT id PK
        INTEGER number UK "running number, also across closed cards"
        TEXT label "NULL shows #number"
        INTEGER created_at
        INTEGER closed_at "NULL while open"
    }
    bingo_slot {
        TEXT bingo_card_id PK
        INTEGER position PK "0..24, row-major"
        TEXT wish_id FK
        INTEGER marked_at "NULL until marked; frozen once the card closes"
    }
    wish_status_change {
        INTEGER id PK "AUTOINCREMENT"
        TEXT wish_id FK
        TEXT status
        TEXT bingo_card_id FK "card it sat on at the time, else NULL"
        INTEGER changed_at
    }
    wish_title_change {
        INTEGER id PK "AUTOINCREMENT"
        TEXT wish_id FK
        TEXT title
        INTEGER changed_at
    }
    app_preference {
        TEXT pref_key PK
        TEXT pref_value
    }
```

- Every foreign key is `ON DELETE RESTRICT`. SQLite enforces them only when the connection asks, so each `DatabaseDriverFactory` and the test driver turn them on
- Timestamps are epoch milliseconds
- `app_preference` is a key-value table for flags such as onboarding completion; it is unrelated to the bingo model
