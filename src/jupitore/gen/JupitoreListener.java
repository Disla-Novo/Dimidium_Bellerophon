// Generated from Jupitore.g4 by ANTLR 4.9.3
package jupitore.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JupitoreParser}.
 */
public interface JupitoreListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(JupitoreParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(JupitoreParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#macro}.
	 * @param ctx the parse tree
	 */
	void enterMacro(JupitoreParser.MacroContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#macro}.
	 * @param ctx the parse tree
	 */
	void exitMacro(JupitoreParser.MacroContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(JupitoreParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(JupitoreParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(JupitoreParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(JupitoreParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#repeat_statement}.
	 * @param ctx the parse tree
	 */
	void enterRepeat_statement(JupitoreParser.Repeat_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#repeat_statement}.
	 * @param ctx the parse tree
	 */
	void exitRepeat_statement(JupitoreParser.Repeat_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#brepeat_statement}.
	 * @param ctx the parse tree
	 */
	void enterBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#brepeat_statement}.
	 * @param ctx the parse tree
	 */
	void exitBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#layer_statement}.
	 * @param ctx the parse tree
	 */
	void enterLayer_statement(JupitoreParser.Layer_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#layer_statement}.
	 * @param ctx the parse tree
	 */
	void exitLayer_statement(JupitoreParser.Layer_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#statement_block}.
	 * @param ctx the parse tree
	 */
	void enterStatement_block(JupitoreParser.Statement_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#statement_block}.
	 * @param ctx the parse tree
	 */
	void exitStatement_block(JupitoreParser.Statement_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(JupitoreParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(JupitoreParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#coordList}.
	 * @param ctx the parse tree
	 */
	void enterCoordList(JupitoreParser.CoordListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#coordList}.
	 * @param ctx the parse tree
	 */
	void exitCoordList(JupitoreParser.CoordListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#coord}.
	 * @param ctx the parse tree
	 */
	void enterCoord(JupitoreParser.CoordContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#coord}.
	 * @param ctx the parse tree
	 */
	void exitCoord(JupitoreParser.CoordContext ctx);
	/**
	 * Enter a parse tree produced by the {@code number}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber(JupitoreParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code number}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber(JupitoreParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parens}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(JupitoreParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(JupitoreParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterator}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIterator(JupitoreParser.IteratorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterator}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIterator(JupitoreParser.IteratorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variable}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVariable(JupitoreParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variable}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVariable(JupitoreParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(JupitoreParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(JupitoreParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(JupitoreParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(JupitoreParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code power}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPower(JupitoreParser.PowerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code power}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPower(JupitoreParser.PowerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mulDiv}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(JupitoreParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mulDiv}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(JupitoreParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#func}.
	 * @param ctx the parse tree
	 */
	void enterFunc(JupitoreParser.FuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#func}.
	 * @param ctx the parse tree
	 */
	void exitFunc(JupitoreParser.FuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link JupitoreParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(JupitoreParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JupitoreParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(JupitoreParser.ConditionContext ctx);
}