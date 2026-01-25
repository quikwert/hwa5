import java.util.*;

/** Tree with two pointers.
 * @since 1.8
 */
public class Tnode {

   private String name;
   private Tnode firstChild;
   private Tnode nextSibling;

   @Override
   public String toString() {
      StringBuffer b = new StringBuffer();
      // TODO!!!
      return b.toString();
   }

   public static Tnode buildFromRPN (String pol) {
      Tnode root = null;
      // TODO!!!
      return root;
   }

   public static void main (String[] param) {
      String rpn = "1 2 +";
      System.out.println ("RPN: " + rpn);
      Tnode res = buildFromRPN (rpn);
      System.out.println ("Tree: " + res);
      // TODO!!! Your tests here
   }
}

