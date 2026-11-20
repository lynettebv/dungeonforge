#!/usr/bin/env python3
"""
WEEK 12 (PROVIDED) -- prints a Java file with its comments removed.

Every counting command in this week's evidence sheet pipes through this, and the reason is a
mistake your instructor has now made nine times in one semester: a grep for a thing finds the
COMMENT that talks about the thing. The reference solution's own javadoc contains the words
`ctx.syncState()` and `new GameWorld(`, so a bare grep reports two of each when the code has
one of each.

    python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'syncState'

Grep the code, not the commentary.
"""
import re, sys, pathlib

for path in sys.argv[1:]:
    s = pathlib.Path(path).read_text(errors="replace")
    s = re.sub(r"(?s)/\*.*?\*/", "", s)     # block comments, javadoc included
    s = re.sub(r"(?m)//.*$", "", s)         # line comments
    print(s)
