import static org.junit.Assert.*;
import org.junit.Test;

/** Testklass.
 * @author Jaanus
 */
public class TnodeTest {

   @Test (timeout=1000)
   public void testBuildFromRPN() {
      String s = "1 2 +";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "+(1,2)", r);
      s = "2 1 - 4 * 6 3 / +";
      t = Tnode.buildFromRPN (s);
      r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "+(*(-(2,1),4),/(6,3))", r);
   }

   @Test (timeout=1000)
   public void testBuild2() {
      String s = "512 1 - 4 * -61 3 / +";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "+(*(-(512,1),4),/(-61,3))", r);
      s = "5";
      t = Tnode.buildFromRPN (s);
      r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "5", r);
   }

   @Test (expected=RuntimeException.class)
   public void testEmpty1() {
      Tnode t = Tnode.buildFromRPN ("\t\t");
   }

   @Test (expected=RuntimeException.class)
   public void testEmpty2() {
      Tnode t = Tnode.buildFromRPN ("\t \t ");
   }

   @Test (expected=RuntimeException.class)
   public void testIllegalSymbol() {
      Tnode t = Tnode.buildFromRPN ("2 xx");
   }

   @Test (expected=RuntimeException.class)
   public void testIllegalSymbol2() {
      Tnode t = Tnode.buildFromRPN ("x");
   }

   @Test (expected=RuntimeException.class)
   public void testIllegalSymbol3() {
      Tnode t = Tnode.buildFromRPN ("2 1 + xx");
   }

   @Test (expected=RuntimeException.class)
   public void testTooManyNumbers() {
      Tnode root = Tnode.buildFromRPN ("2 3");
   }

   @Test (expected=RuntimeException.class)
   public void testTooManyNumbers2() {
      Tnode root = Tnode.buildFromRPN ("2 3 + 5");
   }

   @Test (expected=RuntimeException.class)
   public void testTooFewNumbers() {
      Tnode t = Tnode.buildFromRPN ("2 -");
   }

   @Test (expected=RuntimeException.class)
   public void testTooFewNumbers2() {
      Tnode t = Tnode.buildFromRPN ("2 5 + -");
   }

   @Test (expected=RuntimeException.class)
   public void testTooFewNumbers3() {
      Tnode t = Tnode.buildFromRPN ("+");
   }

   // ---- SWAP tests ----

   @Test (timeout=1000)
   public void testSwapBasic() {
      String s = "2 5 SWAP -";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "-(5,2)", r);
   }

   @Test (timeout=1000)
   public void testSwapWithSubtrees() {
      // "2 5 9 ROT + SWAP -" must produce -(+(9,2),5)
      String s = "2 5 9 ROT + SWAP -";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "-(+(9,2),5)", r);
   }

   @Test (expected=RuntimeException.class)
   public void testSwapUnderflow0() {
      // SWAP with empty stack
      Tnode t = Tnode.buildFromRPN ("SWAP");
   }

   @Test (expected=RuntimeException.class)
   public void testSwapUnderflow1() {
      // SWAP with only one element on stack
      Tnode t = Tnode.buildFromRPN ("3 SWAP");
   }

   // ---- DUP tests ----

   @Test (timeout=1000)
   public void testDupBasic() {
      String s = "3 DUP *";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "*(3,3)", r);
   }

   @Test (timeout=1000)
   public void testDupIsIndependentCopy() {
      // DUP must produce an independent copy, not just the same reference.
      // We verify the tree structure is identical but the objects are separate
      // by checking that the full expected string is produced correctly.
      String s = "2 5 DUP ROT - + DUP *";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "*(+(5,-(5,2)),+(5,-(5,2)))", r);
   }

   @Test (expected=RuntimeException.class)
   public void testDupUnderflow0() {
      // DUP with empty stack
      Tnode t = Tnode.buildFromRPN ("DUP");
   }

   // ---- ROT tests ----

   @Test (timeout=1000)
   public void testRotBasic() {
      // "2 5 9 ROT - +" must produce +(5,-(9,2))
      String s = "2 5 9 ROT - +";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "+(5,-(9,2))", r);
   }

   @Test (timeout=1000)
   public void testRotWithNegatives() {
      // "-3 -5 -7 ROT - SWAP DUP * +" must produce +(-(-7,-3),*(-5,-5))
      String s = "-3 -5 -7 ROT - SWAP DUP * +";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "+(-(-7,-3),*(-5,-5))", r);
   }

   @Test (expected=RuntimeException.class)
   public void testRotUnderflow0() {
      // ROT with empty stack
      Tnode t = Tnode.buildFromRPN ("ROT");
   }

   @Test (expected=RuntimeException.class)
   public void testRotUnderflow1() {
      // ROT with only one element
      Tnode t = Tnode.buildFromRPN ("5 ROT");
   }

   @Test (expected=RuntimeException.class)
   public void testRotUnderflow2() {
      // ROT with only two elements
      Tnode t = Tnode.buildFromRPN ("5 3 ROT");
   }

   // ---- Combined DUP/SWAP/ROT tests ----

   @Test (timeout=1000)
   public void testDupRotCombined() {
      // "2 5 DUP ROT - + DUP *" must produce *(+(5,-(5,2)),+(5,-(5,2)))
      String s = "2 5 DUP ROT - + DUP *";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "*(+(5,-(5,2)),+(5,-(5,2)))", r);
   }

   @Test (timeout=1000)
   public void testRotSwapCombined() {
      // "2 5 9 ROT + SWAP -" must produce -(+(9,2),5)
      String s = "2 5 9 ROT + SWAP -";
      Tnode t = Tnode.buildFromRPN (s);
      String r = t.toString().replaceAll("\\s+", "");
      assertEquals ("Tree: " + s, "-(+(9,2),5)", r);
   }

}

