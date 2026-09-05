# Definition of Done — SUPPLIED

> **You do not write this.** It has been written for you and it applies to every story, every
> week, for the rest of the semester.
>
> Your job in Week 2 is to **read it critically**. One of the boxes below is different in
> kind from all the others — it cannot actually be checked. Finding it is part of Lab Part C.
>
> **Why it's written now, before you need it:** a Definition of Done written before you are
> behind schedule is honest. One written while you are behind is a negotiation with yourself
> that you will lose.

A story is **Done** when all of the following are true. Not "mostly." All.

## Code
- [ ] Compiles with zero errors and zero new warnings
- [ ] Every acceptance criterion on the issue is demonstrably met
- [ ] No commented-out code and no leftover debug printing
- [ ] The week's pattern is used where the week's UML says it is
- [ ] The code is well written and easy for someone else to understand

## Tests
- [ ] New behaviour has at least one test
- [ ] `mvn test` is green locally
- [ ] CI reports green on the pull request

## Documentation
- [ ] Public classes and methods have a comment explaining **why** they exist, not what they do
- [ ] The week's UML diagram matches the code as merged
- [ ] README updated if visible behaviour changed

## Process
- [ ] Work happened on a feature branch, never directly on `main`
- [ ] Commit messages follow Conventional Commits
- [ ] A pull request was opened, self-reviewed, and merged
- [ ] The issue was closed by the pull request (`Closes #NN`)
- [ ] The release was tagged

---

## What "not done" means

A story that fails any box goes **back on the board**. It does not get merged with a note
saying "will fix later." That note is exactly how a semester ends up with fourteen
half-finished patterns.

## The test for a good criterion

> **Could two reasonable people disagree about whether this box is true?**

If yes, it isn't a criterion — it's an opinion wearing a checkbox. Every box above should
survive that test.

One of them doesn't.
