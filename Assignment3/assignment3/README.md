# assignment3

A Quil sketch program.

## Usage

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'assignment3.core)`.

The function that must be executed to start the program is defsketch.

The program reads from a file named turtleTester.txt in the resources directory. The format of the commands in the file is:

pen up
pen down
move <an integer value>
turn <an integer value>

