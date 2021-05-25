# Extract Reference
To extract reference documentation from Tego declarations, one approach might be:

1. annotate the AST with the term origins (locations) (or gather the locations through other means);
2. perform a regex search on the code to find all the documentation blocks (either they start with `/**` and end with `*/`, or they start with `///` and end at the end of the line);
3. merge multiple consecutive documentation blocks into one, separated by newlines;
4. associate documentation blocks with their corresponding declarations (usually the next declaration, next parameter declaration or next module declaration);
5. parse the doc blocks (if necessary), such as extracting info in parameters or return values;
6. generating documentation XML;
7. generating markdown from XML.

This approach may also be viable for other languages.