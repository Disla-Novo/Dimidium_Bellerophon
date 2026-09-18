package maindeveloper.core;

import jupitore.gen.*;

public class Compute extends JupitoreBaseVisitor<Double> {

    private int iteration;
    private int line = 1;
    private GCodeVisitor visitor;

    public Compute(GCodeVisitor visitor, int iteration) {
        this.visitor = visitor;
        this.iteration = iteration;
    }

    public void setIteration(int i) {
        this.iteration = i;
    }

    public void setLine(int l) {
        this.line = l;
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

    // 7/21/26 unary minus for negative numbers in expressions
    @Override
    public Double visitUnaryMinus(JupitoreParser.UnaryMinusContext ctx) {
        return -visit(ctx.expr());
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
        // System.out.println("MULDIV: left=" + left + ", right=" + right);
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
        double value = visit(ctx.expr());
        String funcName = ctx.func().getText().toLowerCase();

        switch (funcName) {
            case "sin":
                return Math.sin(Math.toRadians(value));
            case "cos":
                return Math.cos(Math.toRadians(value));
            case "tan":
                return Math.tan(Math.toRadians(value));

            case "sinr":
                return Math.sin(value);
            case "cosr":
                return Math.cos(value);
            case "tanr":
                return Math.tan(value);

            case "deg":
                return value * 180.0 / Math.PI;

            case "sqrt":
                if (value < 0) {
                    throw new BellerophonException(line,
                            "ERROR: Cannot calculate square root of a negative number: " + value);
                }
                return Math.sqrt(value);
            case "abs":
                return Math.abs(value);
            case "sign":
                return Math.signum(value);
            default:
                throw new BellerophonException(line, "Unknown function: " + funcName);
        }
    }

    @Override
    public Double visitVariable(JupitoreParser.VariableContext ctx) {
        String varName = ctx.ID().getText();
        if (visitor.localVariables.containsKey(varName)) {
            return visitor.localVariables.get(varName);
        }
        if (visitor.globalVariables.containsKey(varName)) {
            return visitor.globalVariables.get(varName);
        }
        throw new BellerophonException(line,
                "ERROR: Undefined variable: '" + varName + "'. " +
                        "Variables must be assigned before they're used.");
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
            throw new BellerophonException(line, "Invalid number at " + context + ": " + value);
        }
    }
}
