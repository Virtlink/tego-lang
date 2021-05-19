# Modifiers

## Declaration Modifiers
A declaration can be marked with one or more of the following modifiers (but note that not all combinations are valid):

- `public` — The declaration is public, visible from outside the module. (Otherwise: private, visible only in the module.)
- `extern` — The declaration has a definition which is provided externally. (Otherwise: declaration must be provided in the module.)

## Module Modifiers
A module can be marked with one or more of the following modifiers (but note that not all combinations are valid):

- `extern` — The declarations in the module are public by default and provided externally. (Otherwise: declarations are private by default.)