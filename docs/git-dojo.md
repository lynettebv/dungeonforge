# Git Dojo — my recovery notes

> Part D of Lab 2. For each drill: the command(s) you ran, **one sentence in your own
> words** on what it did, and one on when you would reach for it again.
>
> Graded on the sentences, not the commands. Commands can be copied; understanding cannot.

## The three trees — in my own words

| Tree | What lives here                |
|---|--------------------------------|
| Working Directory | Files I am working on.         |
| Staging Area (Index) | Changes for my next commit.    |
| HEAD | Files from most recent commit. |

---

## Drill 1 — Committed to `main` by accident

**Commands I ran:**
```bash
git switch main
"oops" | Set-Content accident.txt
git add accident.txt
git commit -m "feat: work that should have been on a branch"
git switch -c fix/rescued-work
git switch main
git reset --hard origin/main
```
**What it did:**
I commited my work directly to main when I wasn't supposed to, so Drill 1 taught me 
to move this work to a new branch and reset main to its state before the accidental commit.

**When I would use it again:**
I would use this again if I ever commit my work to the wrong branch again.
---

## Drill 2 — Wrong commit message / forgot a file

**Commands I ran:**
```bash
"x" | Set-Content note.txt
git add note.txt
git commit -m "asdf"
git commit --amend -m "docs: add note file"

"y" | Set-Content note2.txt
git add note2.txt
git commit --amend --no-edit
```
**What it did:**
Drill 2 helped me fix my most recent comment by amending the commit in order to
correct the mistake instead of starting over. I fixed an incorrect commit message and included
a file that was forgotten.

**Why you must not do this to a commit you already pushed:**
You should not do this to a commit you already published because
it could mess with you team in a group setting. If I amend after a push,
my computer would have the amended/new version, but my team would still be
working on the original, pushed version.
---

## Drill 3 — Committed a file that should be ignored

**Commands I ran:**
```bash
"junk" | Set-Content target/Main.class
git add -f target/Main.class
git commit -m "chore: oops, committed build output"

git rm -r --cached target
echo "target/" >> .gitignore
git add .gitignore
git commit -m "chore: untrack build output and ignore target/"
```
**What it did:**
Drill 3 was used to stop Git from tracking a file that should have been ignored, 
yet still kept on my computer.

**Why adding it to `.gitignore` alone was not enough:**
This was not enough because the file was already being tracked by Git,
therefore it had to be untracked before we could ask .gitignore to
stop Git from tracking it once again.
---

## Drill 4 — Merge conflict

**Commands I ran:**
```bash
git switch main
git switch -c feature/a
git commit -am "docs: title from branch A"

git switch main
git switch -c feature/b
git commit -am "docs: title from branch B"

git switch main
git merge feature/a
git merge feature/b
```
**In the conflict markers, which side was "mine"?**
I would say branch A, since it was merged into main first and then Branch B is what brought the conflict.

**What it did:**
Drill 4 helped me fix what happens when two branches make different changes
to the same part of a file. I fixed the conflict myself and chose what to keep and
then let Git know that the conflict was fixed because Git doesn't decide that by itself.

**How I would back out of a merge I regretted starting:**
git merge --abort
---

## Drill 5 — "I destroyed everything"

**Commands I ran:**
```bash
git log --oneline
git reset --hard HEAD~3
git log --oneline
git reflog
git reset --hard <hash I needed to get back to>
```
**What `git reflog` showed me:**
It showed me where HEAD had been so far, even including the commits that I had
deleted on purpose.

**One sentence on why this changes how nervous I should be about Git:**
This helps so much because if I ever get that sinking feeling that I lost
all my work, I can find it with `git reflog`!
---

## Stretch — Drill 6 (detached HEAD, interactive rebase)

**Notes:**
The detached HEAD means I am looking at a specific commit instead
of being on a branch. The interactive rebase allows me to clean
up commit history.

---

## The one command I want to remember from today

`git reflog`