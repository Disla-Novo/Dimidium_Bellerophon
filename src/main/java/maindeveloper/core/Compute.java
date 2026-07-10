package maindeveloper.core;

import jupitore.gen.*;

public class Compute extends JupitoreBaseVisitor<Double> {

    private int iteration;
    private GCodeVisitor visitor;

    public Compute(GCodeVisitor visitor, int iteration) {
        this.visitor = visitor;
        this.iteration = iteration;
    }

    /**
     * @param ctx
     * @return Double
     */
    // number literal
    @Override
    public Double visitNumber(JupitoreParser.NumberContext ctx) {
        return parseDoubleSafe(ctx.NUMBER().getText(), "number literal");
    }

    /**
     * @param ctx
     * @return Double
     */
    // iterator "i"
    @Override
    public Double visitIterator(JupitoreParser.IteratorContext ctx) {
        return (double) iteration;
    }

    
    /**
     * @param ctx
     * @return Double
     */
    // pi constant
    @Override
    public Double visitPi(JupitoreParser.PiContext ctx) {
        return Math.PI;
    }

    
    /**
     * @param ctx
     * @return Double
     */
    // parentheses
    @Override
    public Double visitParens(JupitoreParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    // NEW
    @Override
    public Double visitPower(JupitoreParser.PowerContext ctx) {
        double base = visit(ctx.expr(0));
        double exponent = visit(ctx.expr(1));
        return Math.pow(base, exponent);
    }

    /**
     * @param ctx
     * @return Double
     */
    // addition / subtraction

    // 4/3/2026: Use op.getText() for simpler operator dispatch
    @Override
    public Double visitAddSub(JupitoreParser.AddSubContext ctx) {

        double left = visit(ctx.expr(0));
        double right = visit(ctx.expr(1));

        return ctx.op.getText().equals("+") ? left + right : left - right;
    }

    /**
     * @param ctx
     * @return Double
     */
    // multiply / divide
    // 4/3/2026: Operator dispatch via op.getText()
    @Override
    public Double visitMulDiv(JupitoreParser.MulDivContext ctx) {
        Double left = visit(ctx.expr(0));
        Double right = visit(ctx.expr(1));
        System.out.println("MULDIV: left=" + left + ", right=" + right);
        if (left == null || right == null) {
            System.out.println("WARNING: left or right is null!");
            return 0.0;
        }
        return ctx.op.getText().equals("*") ? left * right : left / right;
    }

    /**
     * @param ctx
     * @return Double
     */
    // functions: sin cos tan sqrt
    // adding Math.toRadians 4/3/2026
    @Override
    public Double visitFuncCall(JupitoreParser.FuncCallContext ctx) {
        // 4/3/2026: Case-insensitive function matching
        double value = visit(ctx.expr());
        String funcName = ctx.func().getText().toLowerCase();

        switch (funcName) {

            case "sin":
                return Math.sin(Math.toRadians(value));

            case "cos":
                return Math.cos(Math.toRadians(value));

            case "tan":
                return Math.tan(Math.toRadians(value));
            // added safety check for sqrt of negative number 6/19/26
            case "sqrt":
                if (value < 0) {
                    throw new RuntimeException("ERROR: Cannot calculate square root of a negative number: " + value);
                }
                return Math.sqrt(value);
            case "abs":
                return Math.abs(value);
            case "sign":
                return Math.signum(value);
            default:
                throw new RuntimeException("Unknown function: " + funcName);
        }
    }

    @Override
    public Double visitVariable(JupitoreParser.VariableContext ctx) {
        String varName = ctx.ID().getText();
        // a macro-local variable shadows a global one of the same name
        Double value = visitor.localVariables.containsKey(varName)
                ? visitor.localVariables.get(varName)
                : visitor.globalVariables.getOrDefault(varName, 0.0);
        System.out.println("READ VAR: " + varName + " = " + value);
        return value;
    }

    @Override
    protected Double defaultResult() {
        return 0.0;
    }


// added helper method 
    private double parseDoubleSafe(String value, String context) {
    try {
        return Double.parseDouble(value);
    } catch (NumberFormatException e) {
        throw new RuntimeException("Invalid number at " + context + ": " + value);
    }
}
}
