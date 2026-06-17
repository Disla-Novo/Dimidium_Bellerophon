// Generated from grammar/Jupitore.g4 by ANTLR 4.9.3
package jupitore.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JupitoreParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JupitoreVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(JupitoreParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#macro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacro(JupitoreParser.MacroContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(JupitoreParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(JupitoreParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#repeat_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat_statement(JupitoreParser.Repeat_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#brepeat_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#layer_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLayer_statement(JupitoreParser.Layer_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#statement_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_block(JupitoreParser.Statement_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(JupitoreParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#coordList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoordList(JupitoreParser.CoordListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#coord}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoord(JupitoreParser.CoordContext ctx);
	/**
	 * Visit a parse tree produced by the {@code number}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(JupitoreParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(JupitoreParser.ParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iterator}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterator(JupitoreParser.IteratorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variable}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(JupitoreParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(JupitoreParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(JupitoreParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code power}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(JupitoreParser.PowerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mulDiv}
	 * labeled alternative in {@link JupitoreParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDiv(JupitoreParser.MulDivContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(JupitoreParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link JupitoreParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(JupitoreParser.ConditionContext ctx);
}