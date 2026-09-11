# Artifact Review Clinic — Lab 2, Part C

> **This is the only document you write this week.** Everything else — the epics, the
> stories, the acceptance criteria, the Definition of Done, the sprint plans — was written
> for you.
>
> Reading critically is a harder and more useful skill than writing from a blank page, and it
> is the one that will make your own stories good when you start writing them in Week 6.

Read all three before answering:
- `docs/backlog.md`
- `docs/definition-of-done.md`
- `docs/sprint-01-plan.md`

---

## C1 — Find the three planted flaws · 12 pts

There is **exactly one deliberate defect in each of the three documents**: one bad user
story, one unverifiable Definition-of-Done criterion, and one sprint-plan item that isn't
what it claims to be.

> **Hint for the story:** re-read INVEST first. The bad one fails more than one letter.
>
> **Hint for the DoD:** ask of every checkbox — *could two reasonable people disagree about
> whether this is true?* If yes, it isn't a criterion. It's an opinion.

Independent: User stories can stand on their own and do not rely on other user stories.
Negotiable: User stories are open to discussion and can be refined and modified based on feedback from stakeholders.
Valuable: User stories provide value to the user and the business.
Estimable: User stories can be estimated in terms of time and effort required for implementation.
Simple: User stories are short and simple descriptions of a feature or functionality.
Testable: User stories can be tested to ensure they meet the needs of the user.

### Flaw 1 — in `docs/backlog.md`

**Which item:** US-1.4

**What's wrong with it:**
**Which INVEST letter(s) it violates, and how:**
N: violates negotiable. Tells developer to use a HashMap with double-locking
V: subjective professional code. subjective to the developer
T: "more Professional code" is subjective

**My repaired version:**

Pick a real beneficiary and define in measurable terms what better code is.
The performance of this code increased by 5%.
(Preferably, you would remove this story all together.)
```
As a ...,
I want ...,
so that ...

Acceptance Criteria
- Given ..., when ..., then ...
- Given ..., when ..., then ...
```

---

### Flaw 2 — in `docs/definition-of-done.md`

**Which checkbox:**
"The code is well written and easy for someone else to understand"

**Why it can't actually be checked:**
Two reasonable people could disagree about whether this box is true,
it cannot be checked by a machine. It is an opinion.

**My replacement, phrased so that it can be:**
Every public class has a comment stating why it exists.
All code standards are verified, naming conventions, code blocks.
All classes, methods, and variables use descriptive names that clearly identify their purpose.

---

### Flaw 3 — in `docs/sprint-01-plan.md`

**Which item:** I might get busy this week.

**Why it isn't really what the document calls it:**
This risk can't be mitigated and is a forever risk.

**My repaired version, including a mitigation someone could actually act on:**
Monday and Tuesday are unavailable, so 3 of the 8 points must be done by Wednesday.

---

## C2 — Say what's good, and why · 9 pts

Pick the **three strongest user stories** in `docs/backlog.md`. For each, two or three
sentences.

> Praise is harder than criticism, and it's where most of the learning is. "It's clear" earns
> nothing. "Its third criterion names an observable output — the same object reference — so
> two people would always agree whether it passed" earns full marks.

### Strong story 1: ______ US-1.2

**INVEST letters it satisfies especially well:**
Testable: All 4 of its acceptance criteria can be checked by a machine.
Valuable: The So that... bug can be reproduced is VERY valuable.

**What specifically makes its acceptance criteria checkable:**
Super machine checkable.
There are no opinions in the acceptance criteria, one is a boolean yes/no check,
another is an existence check or absence check.

### Strong story 2: ______ US-1.1

**INVEST letters it satisfies especially well:**
Negotiable: States its need, but leaves the implementation up to the developer.
Valuable: strictly names the game designer as the beneficiary.

**What specifically makes its acceptance criteria checkable:**
Acceptance criteria sets player hit points to 80, this is machine checkable.

### Strong story 3: ______ S0.2

**INVEST letters it satisfies especially well:**
Small: 2 quick points and 1 workflow file
Independent: Only repo is necessary for this user story

**What specifically makes its acceptance criteria checkable:**
Easily verifiable by machine (i.e. verified with screenshots)

---

## C3 — Trace a story to code · 4 pts

Take **US-1.1** (settings live in one place). **Write no Java.** In plain English, describe
what you'd expect to see in the pull-request diff when this story is done, and which
acceptance criterion each piece satisfies.

| What I'd expect in the diff | Which acceptance criterion it satisfies |
|-----------------------------|----------------------------------------|
| config.json                 | AC1                                    |
| Config class                | AC3                                    |
| GameWorld class             | AC2                                    |
| Main class                  | AC1                                    |
| getInstance Method          | AC1                                    |

**One sentence: how did the acceptance criteria help you predict the shape of the work?**
The acceptance criteria stated a need that dictated the creation of a class or type.

---

## C4 — The bonus catch · up to +3 bonus

Once you have dealt with the bad story, something in `docs/sprint-01-plan.md` no longer adds
up the way it did.

**What is it:** 
Once we've removed the bad story, which is better than just fixing it,
our committed points would change from 13 points to 8. 
Removal of the defective story brings us under the capacity of 10 points.

**What a real team would do about it in sprint planning:**
The team would not necessarily need to add anything to the sprint, but it would be worth it to consider
adding a solid user story from the backlog into the mix. 

**What this suggests about the relationship between vague work and over-committed sprints:**
Vague work may lead the team to an over-commited sprint because it is hard
to estimate with much accuracy.

---

## C5 — One honest question

What is one thing about the Scrum process you still don't understand after this week? A good
question here is worth more to me than a confident wrong answer.

I feel like I would struggle a bit to write my own acceptable user story.
I do like that I can be guided and can double-check my story with INVEST,
but I feel at a bit of loss to even create one. It's nice that they are created for us,
and practice fixing the defective one does help understand the creation.
However, I just feel a bit overwhelmed by this concept.

