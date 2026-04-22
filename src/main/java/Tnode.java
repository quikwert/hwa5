import java.util.*;
import java.util.function.Supplier;

/** Tree with two pointers.
 * @since 1.8
 */
public class Tnode {

	private String name;
	private Tnode firstChild;
	private Tnode nextSibling;

	public void setSibling(Tnode sibling){
		nextSibling = sibling;
	}

	public void setChild(Tnode child){
		firstChild = child;
	}

	public Tnode deepCopy(){
		Tnode copy = new Tnode(this.name);

        if (this.firstChild != null) {
            copy.firstChild = this.firstChild.deepCopy();
        }

        if (this.nextSibling != null) {
            copy.nextSibling = this.nextSibling.deepCopy();
        }

        return copy;	
	}


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
		if(OperationInstruction.isOperation(node.name)){
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
			if (isOperand(token)) {
				stack.push(new Tnode(token));
			} else {

				Instruction instruction = OperationRegistry.get(token);

				if (instruction == null) {
					throw new IllegalArgumentException("Unknown token: " + token);
				}

				instruction.execute(stack);
			}
		}
		Tnode ret = stack.pop();
		if(!stack.empty()){
			throw new RuntimeException("Too many numbers in expression: " + pol);
		}
		return ret;
	}
	static boolean isOperand(String token){
		try{
			Double.parseDouble(token);
		}catch (RuntimeException e){
			return false;
		}	
		return true;
	}
	public static void main (String[] param) {
		String rpn = "2 5 SWAP -";
		System.out.println ("RPN: " + rpn);
		Tnode res = buildFromRPN (rpn);
		System.out.println ("Tree:\n " + res);
	}

}

interface Operation{
	public int arity();
	public Tnode apply (List<Tnode> operands);
}

interface Instruction{
	public void execute(Stack<Tnode> stack);	
}

class OperationInstruction implements Instruction {

	private final Operation operation;

	public OperationInstruction(Operation operation) {
		this.operation = operation;
	}

	static boolean isOperation(String token){

		Instruction ret = OperationRegistry.get(token);
		if(ret == null) return false;
		return true;
	}
	@Override
	public void execute(Stack<Tnode> stack) {
		int arity = operation.arity();

		List<Tnode> operands = new ArrayList<>(arity);

		// maintain correct order
		for (int i = 0; i < arity; i++) {
			operands.add(0, stack.pop());
		}

		Tnode result = operation.apply(operands);
		stack.push(result);
	}
}


class OperationRegistry {
	private static final Map<String, Instruction> operations = Map.of(
			"+", new OperationInstruction(new AddOperation()),
			"-", new OperationInstruction(new SubtractOperation()),
			"*", new OperationInstruction(new MultiplyOperation()),
			"/", new OperationInstruction(new DivideOperation()),
			"SWAP", new SwapInstruction(),
			"DUP", new DupInstruction(),
			"ROT", new RotInstruction()
			);



	public static Instruction get(String token) {
		Instruction ret = operations.get(token); 
		return ret;
	}
}

class AddOperation implements Operation{
	@Override
	public int arity(){ return 2 ;}

	@Override
	public Tnode apply(List<Tnode> operands){

		Tnode node = new Tnode("+");
		Tnode first = null;
		Tnode youngerSibling = null;

		for (int i = 1; i >= 0 ; i--){
			Tnode child;		
			child = operands.get(i);
			if(i == 0) first = child;
			child.setSibling(youngerSibling);

			youngerSibling = child;
		}

		node.setChild(first);
		return node;
	}
}

class SubtractOperation implements Operation{
	@Override
	public int arity(){ return 2 ;}

	@Override
	public Tnode apply(List<Tnode> operands){

		Tnode node = new Tnode("-");
		Tnode first = null;
		Tnode youngerSibling = null;

		for (int i = 1; i >= 0 ; i--){
			Tnode child;		
			child = operands.get(i);
			if(i == 0) first = child;
			child.setSibling(youngerSibling);

			youngerSibling = child;
		}

		node.setChild(first);
		return node;
	}
}

class MultiplyOperation implements Operation{

	@Override
	public int arity(){ return 2 ;}

	@Override
	public Tnode apply(List<Tnode> operands){

		Tnode node = new Tnode("*");
		Tnode first = null;
		Tnode youngerSibling = null;

		for (int i = 1; i >= 0 ; i--){
			Tnode child;		
			child = operands.get(i);
			if(i == 0) first = child;
			child.setSibling(youngerSibling);

			youngerSibling = child;
		}

		node.setChild(first);
		return node;
	}
}

class DivideOperation implements Operation{
	@Override
	public int arity(){ return 2 ;}

	@Override
	public Tnode apply(List<Tnode> operands){

		Tnode node = new Tnode("/");
		Tnode first = null;
		Tnode youngerSibling = null;

		for (int i = 1; i >= 0 ; i--){
			Tnode child;		
			child = operands.get(i);
			if(i == 0) first = child;
			child.setSibling(youngerSibling);

			youngerSibling = child;
		}

		node.setChild(first);
		return node;
	}
}

class SwapInstruction implements Instruction{
	@Override
	public void execute(Stack<Tnode> stack) {
		if(stack.size() < 2) throw new RuntimeException("Too little arguments!");
		Tnode second = stack.pop();
		Tnode first = stack.pop();
		stack.push(second);
		stack.push(first); 
	}
}

class DupInstruction implements Instruction{
	@Override
	public void execute(Stack<Tnode> stack) {
		if(stack.size() < 1) throw new RuntimeException("Too little arguments!");
		stack.push(stack.peek().deepCopy());
	}
}

class RotInstruction implements Instruction{
	@Override
	public void execute(Stack<Tnode> stack) {
		if(stack.size() < 3) throw new RuntimeException("Too little arguments!");
		Tnode third = stack.pop();
		Tnode second = stack.pop();
		Tnode first = stack.pop();
		stack.push(second); 
		stack.push(third); 
		stack.push(first);
	}
}


