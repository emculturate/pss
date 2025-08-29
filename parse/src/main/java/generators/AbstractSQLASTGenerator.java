package generators;

public class AbstractSQLASTGenerator {
    // This class provides a basic foundation for walking through SQL ASTs
    // in a depth-first search. As it proceeds it will call stubbed out methods 
    // that can be overridden by subclasses to handle specific SQL constructs.
    // Each stubbed out method will take one node of the SQL AST and will create an output
    // text representation of that node. Different actual generator classes will produce different kinds of output,
    // such as SQL statements, or other text based things.

    
}
