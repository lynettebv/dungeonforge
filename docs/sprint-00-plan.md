# Sprint 0 Plan — Week 2 — SUPPLIED

**Epic:** E2, Process infrastructure

## Sprint Goal

> A repository that builds itself, tests itself, and refuses work that isn't finished.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| S0.1 | The project builds and tests itself | 3 |
| S0.2 | A broken build blocks a merge | 2 |
| S0.3 | The board shows the truth | 2 |

**Capacity:** ~8 points · **Committed:** 7 points

## Risks

| Risk | Mitigation |
|---|---|
| Maven or JDK 21 not installed, blocking every story | Verify `mvn -v` and `java -version` first; IntelliJ bundles Maven if needed |
| `gh` CLI authentication fails, blocking the seed | `docs/backlog.md` contains every issue for manual creation — about twenty minutes |
| Hidden `.github/` folder not copied on macOS | Cmd+Shift+. reveals hidden files in Finder; confirm with `ls -a` |

---

# ↓ YOU fill in these two sections at the end of the week ↓

## Calibration — actual vs. estimate

> Do this every sprint. You are not estimating your own work until Week 6, but the data you
> record now is what makes those estimates useful when you get there. There is no way to
> learn this from theory.

| Story | Estimated points | Actual hours | Was the estimate high, low, or about right? |
|---|---|---|---|
| S0.1 | 3 | | |
| S0.2 | 2 | | |
| S0.3 | 2 | | |

**Points completed (my first velocity number):** ____

## Sprint Review — one sentence

> What can the project do now that it could not do at the start of the week?


