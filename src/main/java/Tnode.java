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
        print(this, "", true, sb);
        return sb.toString();
    }

    private static void print(Tnode node, String prefix, boolean isLast, StringBuilder sb) {
        if (node == null) return;

        sb.append(prefix);
        sb.append(isLast ? "└── " : "├── ");
        sb.append(node.name).append("\n");

        // children use extended prefix
        String childPrefix = prefix + (isLast ? "    " : "│   ");

        Tnode child = node.firstChild;
        while (child != null) {
            boolean last = (child.nextSibling == null);
            print(child, childPrefix, last, sb);
            child = child.nextSibling;
        }
    }

	public static Tnode buildFromRPN (String pol) {
		Stack<Tnode> stack = new Stack<>();
		String[] tokens = pol.split("\\s+");
		for (String token : tokens){
					System.out.println(token);

			if(Operation.isOperation(token)){

				Operation op = Operation.get(token);
				int arity = op.arity; 

				Tnode node = new Tnode(token);
				Tnode first = null;
				Tnode prev = null;

				for (int i = 0; i < arity; i++){
					Tnode child = stack.pop();		

					if(i == 0) first = child;
					else prev.nextSibling = child;

					prev = child;
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

		return stack.pop();
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

