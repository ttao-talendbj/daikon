var TqlLexer = require("./TqlLexer").TqlLexer;
var TqlParser = require("./TqlParser").TqlParser;
var TqlListener = require("./TqlParserListener").TqlParserListener;

var antlr4 = require('antlr4/index');
var parse = (tql, onExactFilter, onContainsFilter, onCompliesFilter,
             onBetweenFilter, onEmptyFilter, onValidFilter, onInvalidFilter) => {

                 var chars = new antlr4.InputStream(tql);
                 var lexer = new TqlLexer(chars);
                 var tokens  = new antlr4.CommonTokenStream(lexer);
                 var parser = new TqlParser(tokens);
                 parser.buildParseTrees = true;
                 var tree = parser.expression();

                 //Define listeners
                 var noop = () => {};
                 var listener = new TqlParserListener();
                 listener.enterLiteralComparison = (ctx) => {
                     switch (ctx.children[1].getText()) {
                     case '=':
                         onExactFilter ? onExactFilter(ctx) : noop();
                         break;
                     default:
                         return noop();
                     }
                 };
                 listener.enterFieldIsEmpty = onEmptyFilter || noop;
                 listener.enterFieldIsValid = onValidFilter || noop;
                 listener.enterFieldIsInvalid = onInvalidFilter || noop;
                 listener.enterFieldContains = onContainsFilter || noop;
                 listener.enterFieldCompliesPattern = onCompliesFilter || noop;
                 listener.enterFieldBetween = onBetweenFilter || noop;

                 //Bind listeners to tree
                 antlr4.tree.ParseTreeWalker.DEFAULT.walk(listener, tree);
             }

export { TqlLexer, TqlParser, TqlListener, parse };
