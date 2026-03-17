package AFN;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GeneradorAFN {

    public static AFN generar(String expresionRegular) {
        List<String> tokens = tokenizarYAgregarConcatenacion(expresionRegular);
        List<String> postfix = aPostfix(tokens);
        return evaluarPostfix(postfix);
    }

    private static List<String> tokenizarYAgregarConcatenacion(String er) {
        List<String> tokens = new ArrayList<>();
        
        for (int i = 0; i < er.length(); i++) {
            char c = er.charAt(i);
            if (c == '[') { 
                int fin = er.indexOf(']', i);
                if (fin != -1) {
                    tokens.add(er.substring(i, fin + 1));
                    i = fin;
                }
            } else {
                tokens.add(String.valueOf(c));
            }
        }

        List<String> tokensConConcat = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String t1 = tokens.get(i);
            tokensConConcat.add(t1);
            if (i + 1 < tokens.size()) {
                String t2 = tokens.get(i + 1);
                if (requiereConcatenacion(t1, t2)) {
                    tokensConConcat.add("°");
                }
            }
        }
        return tokensConConcat;
    }

    private static boolean requiereConcatenacion(String t1, String t2) {
        boolean t1EsOp = esOperando(t1) || t1.equals(")") || t1.equals("*") || t1.equals("+") || t1.equals("?");
        boolean t2EsOp = esOperando(t2) || t2.equals("(");
        return t1EsOp && t2EsOp;
    }

    private static boolean esOperando(String t) {
        return !t.equals("|") && !t.equals("*") && !t.equals("+") && !t.equals("?") &&
               !t.equals("(") && !t.equals(")") && !t.equals("°");
    }

    private static int precedencia(String op) {
        switch (op) {
            case "*": case "+": case "?": return 3; 
            case "°": return 2;                    
            case "|": return 1;                  
            case "(": return 0;
            default: return -1;
        }
    }

    private static List<String> aPostfix(List<String> tokens) {
        List<String> postfix = new ArrayList<>();
        Stack<String> pilaOps = new Stack<>();

        for (String t : tokens) {
            if (esOperando(t)) {
                postfix.add(t);
            } else if (t.equals("(")) {
                pilaOps.push(t);
            } else if (t.equals(")")) {
                while (!pilaOps.isEmpty() && !pilaOps.peek().equals("(")) {
                    postfix.add(pilaOps.pop());
                }
                if (!pilaOps.isEmpty()) pilaOps.pop(); 
            } else { 
      
                while (!pilaOps.isEmpty() && precedencia(pilaOps.peek()) >= precedencia(t)) {
                    postfix.add(pilaOps.pop());
                }
                pilaOps.push(t);
            }
        }
        while (!pilaOps.isEmpty()) {
            postfix.add(pilaOps.pop());
        }
        return postfix;
    }

    private static AFN evaluarPostfix(List<String> postfix) {
 Stack<AFN> pilaAFN = new Stack<>();

        for (String t : postfix) {
  if (esOperando(t)) {
                if (t.startsWith("[")) {
            
                    char c1 = t.charAt(1);
                    char c2 = t.charAt(3);
                    pilaAFN.push(new AFN(c1, c2));
                } else {
            
                    pilaAFN.push(new AFN(t.charAt(0)));
                }
            } else if (t.equals("*")) {
                AFN afn = pilaAFN.pop();
                afn.CerrKleene();
                pilaAFN.push(afn);
            } else if (t.equals("+")) {
                AFN afn = pilaAFN.pop();
                afn.CerrPositiva();
                pilaAFN.push(afn);
            } else if (t.equals("?")) {
                AFN afn = pilaAFN.pop();
                afn.CerrOpcional();
                pilaAFN.push(afn);
            } else if (t.equals("°")) {
                AFN b = pilaAFN.pop(); 
                AFN a = pilaAFN.pop(); 
                a.ConcatenarAFN(b);
                pilaAFN.push(a);
            } else if (t.equals("|")) {
                AFN b = pilaAFN.pop();
                AFN a = pilaAFN.pop();
                a.UnirAFN(b);
                pilaAFN.push(a);
            }
        }

       
        return pilaAFN.pop();
    }
}