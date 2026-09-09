---
name: mokkery
description: Write or fix tests that use mokkery mocks in this repo — picking a matcher (literal, ofType, capture), MockMode, and verify. Use when a test needs a mock, or when a mokkery call compiles but does not behave as expected.
---

# mokkery

Add `id("wishline.mokkery")` to any module that mocks. It is not applied by default.

## Picking a matcher

| Situation | Use |
|---|---|
| The expected value is known | Pass the literal — there is no `eq` matcher to reach for |
| Only the type matters | `ofType<T>()` |
| The value has to be inspected | `capture(...)`, in the `every` block |

## capture belongs in every, not verify

A capture records while a stubbed definition answers a call. Inside `verify` nothing answers, so the
capture stays empty while the verification itself still passes — the failure surfaces later, as an
empty list where a value was expected.

```kotlin
private val status = Capture.slot<WishStatus>()
private val repository = mock<WishRepository> {
    everySuspend { changeStatus(any(), capture(status)) } returns Unit
}
```

To check which subtype was passed, `ofType<T>()` inside `verify` says it directly:

```kotlin
verifySuspend(exactly(1)) {
    repository.changeStatus(WishId("w1"), ofType<WishStatus.Done>())
}
```

## MockMode

`mock<T>(MockMode.autoUnit)` answers Unit-returning functions without stubbing them. Use it when a
test only verifies calls.

Checked against mokkery 3.4.2.
