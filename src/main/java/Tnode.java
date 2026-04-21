import java.util.*;

/** Tree with two pointers.
 * @since 1.8
 */
public class Tnode {

	private String name;
	private Tnode firstChild;
	private Tnode nextSibling;

	Tnode(String name){
		this.name = name;
	}
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(this, sb, true);
        return sb.toString();
    }

    private static void print(Tnode node, StringBuilder sb, boolean isRoot) {
		sb.append(node.name);
	if(Operation.isOperation(node.name)){
		sb.append("(");
	}
	if(node.firstChild != null){
		print(node.firstChild, sb, false);
	}	
	if(node.nextSibling == null ){
		if(!isRoot)sb.append(")");
		return;
	}
	sb.append(", ");
	print(node.nextSibling, sb,false);
	return;
	

    }

	public static Tnode buildFromRPN (String pol) {
		Stack<Tnode> stack = new Stack<>();
		String[] tokens = pol.split("\\s+");
		if(tokens.length == 0) throw new RuntimeException("Cannot build from empty String!");
		for (String token : tokens){
			if(Operation.isOperation(token)){

				Operation op = Operation.get(token);
				int arity = op.arity; 

				Tnode node = new Tnode(token);
				Tnode first = null;
				Tnode youngerSibling = null;

				for (int i = arity - 1; i >= 0 ; i--){
					Tnode child = stack.pop();		

					if(i == 0) first = child;
					child.nextSibling = youngerSibling;

					youngerSibling = child;
				}

				node.firstChild = first;

				stack.push(node);
			}else{
				try{
					Double.parseDouble(token);
					stack.push(new Tnode(token));
				}catch( RuntimeException e){
					throw new RuntimeException("Invalid format for number: " + token);
				}
			}
		}
		Tnode ret = stack.pop();
		if(!stack.empty()){
			throw new RuntimeException("Too many numbers in expression: " + pol);
		}
		return ret;
	}

	public static void main (String[] param) {
		String rpn = "5 1 2 + 4 * + 3 -";
		System.out.println ("RPN: " + rpn);
		Tnode res = buildFromRPN (rpn);
		System.out.println ("Tree:\n " + res);
		// TODO!!! Your tests here
	}

}

class Operation{
	final int arity;
	final Evaluator evaluator;
	interface Evaluator{
		double apply(Tnode node, double[] args);
	}	
	Operation(int arity, Evaluator evaluator){
		this.arity = arity;
		this.evaluator = evaluator;	
	}
	static boolean isOperation(String token){
		return Ops.OPS.containsKey(token);
	}

	static Operation get(String token){
		Operation op = Ops.OPS.get(token);
		if(op == null){
			throw new RuntimeException("Operation not supported: " + token);
		}
		return op;
	}
}

class Ops {
	static final Map<String,Operation> OPS = Map.of(
			"+", new Operation(2, (node, a) -> a[0] + a[1]),
			"-", new Operation(2, (node, a) -> a[0] - a[1]),
			"*", new Operation(2, (node, a) -> a[0] * a[1]),
			"/", new Operation(2, (node, a) -> a[0] / a[1])
			);
}

