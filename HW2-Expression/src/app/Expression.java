package app;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    {	
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	StringTokenizer ST1 = new StringTokenizer(expr, delims, true);
    	Stack <String> tempStack = new Stack<String>();
    	
    	while(ST1.hasMoreTokens()) 
    	{
    		String token1 = ST1.nextToken();
    		
    		if(Character.isDigit(token1.charAt(0)))
    			continue;
    		
    		else if(token1.equals("[")) 
    		{
    			String Out = tempStack.pop();
    			if(arrays.contains(new Array(Out))) 
    			{
    				continue;
    			}
    			else 
    			{
    				arrays.add(new Array(Out));
    			}
    		}
    		
    		else if(op(token1) == true || token1.charAt(0) == '(' || token1.charAt(0) == ')' || 
    				token1.charAt(0) == '[' || token1.charAt(0) == ']' || token1.charAt(0) == ' ') 
    		{
    			continue;
    		}
    		else
    			tempStack.push(token1);
    	}
    	
    	while(tempStack.isEmpty() == false) 
    	{
    		String token2 = tempStack.pop();
    		if(vars.contains(new Variable(token2)))
    			continue;
    		else
    			vars.add(new Variable(token2));
    	}
    }
    
    private static boolean op(String tok) 
    {	
    	if(tok.length() == 1) 
    	{
    		char op;
    		op = tok.charAt(0);
    		if(op == '+' || op == '-' || op == '/' || op == '*')
    			return true;
    	}
		return false;
    }

	/**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
   }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	
    	expr = expr.replaceAll(" ", ""); 		
    	float ans = 0;
    	
    	StringTokenizer s = new StringTokenizer(expr, delims, true);
    	String presentToken = "";
    	
    	char operation = '\0';	
    	Character tempOp = '\0';
    	String validOp = "+-*/";
    	
    	String num1 = "", num2 = "", tempNum = ""; 
    	float index = 0; 
    	
  	  	while (s.hasMoreTokens()) 
  	  	{
  	  		presentToken = s.nextToken();
		  	System.out.println(presentToken);
		  	
		  	if(presentToken.charAt(0) == '[') 
		  	{
		  		index = subExpr('[', ']', s, vars, arrays);
		  		
		  		if (num2.equals("")) 
		  		{
		  			for(int i = 0; i < arrays.size(); i++)
		  			{
		  				if (arrays.get(i).name.equals(num1)) 
		  				{
		  					num1 = arrays.get(i).values[(int)index] + "";
		  					break;
		  				}
		  			}
		  		}
		  		else 
		  		{
		  			for (int j = 0; j < arrays.size(); j++) 
		  			{
		  				if (arrays.get(j).name.equals(num2)) 
		  				{
		  					num2 = arrays.get(j).values[(int)index] + "";
		  					break;
		  				}
		  			}
		  		}
		  	} 
		  	
		  	else if (presentToken.charAt(0) == '(') 
		  	{	
		  		if (num1.equals("")) 
		  		{
		  			num1 = subExpr('(', ')', s, vars, arrays) + "";
		  		}
		  		else 
		  		{
		  			num2 = subExpr('(', ')', s, vars, arrays) + "";
		  		}
		  	} 
		  	else if (Character.isLetter(presentToken.charAt(0)) == true) 
		  	{ 
		  		if (num1.equals("")) 
		  		{
		  			num1 = presentToken;
		  		}
		  		else 
		  		{
		  			num2 = presentToken;
		  		}
		  	}															  
		  	else if (validOp.indexOf(presentToken.charAt(0)) >= 0) 
		  	{	
		  		if (operation == '\0')
		  		{ 
		  			operation = presentToken.charAt(0);
		  		}
		  		else 
		  		{	
		  			if (Character.isLetter(num1.charAt(0)) == true ) 
		  			{
		  				num1 = varToNum(num1, vars);
		  			}
		  			if (Character.isLetter(num2.charAt(0)) == true) 
		  			{
		  				num2 = varToNum(num2, vars);
		  			}
		  			
		  			if (operation == '+' || operation == '-') 
		  			{ 
		  				if (presentToken.charAt(0) == '*' || presentToken.charAt(0) == '/') 
		  				{
		  					tempOp = operation; 
		  					operation = presentToken.charAt(0);
		  					tempNum = num1;
		  					num1 = num2;
		  					num2 = "";
		  				}
		  				else 
		  				{ 
		  					if (operation == '+' || operation == '-') 
		  					{
		  						ans = calculate(num1, num2, operation);
		  						num1 = ans + "";
		  						num2 = "";
		  						operation = presentToken.charAt(0);
		  					}
		  				}
		  			}
		  			else 
		  			{ 									  		  				
		  				if (validOp.indexOf(operation) >= 2) 
		  				{
		  					ans = calculate(num1, num2, operation);
		  					num1 = ans + ""; //Now the ans is moved to be in num
		  					num2 = "";
		  					operation = presentToken.charAt(0);
		  				}
		  				
		  				if (tempOp.equals('\0') == false) 
		  				{ 
		  					if (operation == '+' || operation == '-') 
		  					{
		  						ans = calculate(tempNum, num1, tempOp);
		  					}
		  				}	
		  			} 											
		  		}																				
		  	}
		  	else 
		  	{
		  		if (num1.equals("")) 
		  		{
		  			num1 = presentToken;
		  		}
		  		else 
		  		{
		  			num2 = presentToken;
		  		}
		  	}
  	 }
  	
  	if (Character.isLetter(num1.charAt(0)) == true) 
  	{
  		num1 = varToNum(num1, vars);
  	}
  	
	if (num2.equals("") == false && Character.isLetter(num2.charAt(0)) == true) 
	{
		num2 = varToNum(num2, vars);
	}
  	  
  	if (num2.equals("") == false) 
  	{
  		System.out.println(num1 + " " + num2);
  		ans = calculate(num1, num2, operation);
  		num1 = ans + "";
  	}
  	
  	if (tempOp.equals('+') || tempOp.equals('-')) 
  	{
			ans = calculate(tempNum, num1, tempOp);
			num1 = ans + "";
			tempOp = '\0';
			tempNum = "";
	}
  	
    ans = Float.valueOf(num1);
   	return ans;
    }
    
    private static float subExpr(char bracketOrParent1, char brackOrParent2, StringTokenizer sParam, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	String subExpression = "";
    	int count = 0;
    	String presentToken = sParam.nextToken();
  		
  		if (presentToken.equals(bracketOrParent1 + "")) 
  		{
  			count++;
  		}
  		
  		while (presentToken.charAt(0) != brackOrParent2 && sParam.hasMoreTokens()) 
  		{	
  			subExpression += presentToken;
  			presentToken = sParam.nextToken();
  			
  			if (presentToken.charAt(0) == bracketOrParent1) 
  			{
  				count++;
  			}
  			else if (presentToken.charAt(0) == brackOrParent2 && count != 0) 
  			{
  	 			while (presentToken.charAt(0) == brackOrParent2 && count != 0) 
  	 			{
  	  				subExpression += presentToken;
  	  				presentToken = sParam.nextToken();
  	  				count--;
  	  			}	
  	  		}
  		}
  		return evaluate(subExpression, vars, arrays);
  	}
    
    private static String varToNum(String varName, ArrayList<Variable> vars) 
    {		
    	for (int i = 0; i < vars.size(); i++)
    	{
			if (vars.get(i).name.equals(varName)) 
			{
				return vars.get(i).value + "";  					
	  		}
	  	}
		return null;
    }
    
    private static float calculate(String num1, String num2, char operation) 
    {
    	switch(operation) 
    	{
	    	case '+': return Float.parseFloat(num1) + Float.parseFloat(num2);
	    	case '-': return Float.parseFloat(num1) - Float.parseFloat(num2);
	    	case '*': return Float.parseFloat(num1) * Float.parseFloat(num2);
	    	case '/':
	    		if (Float.parseFloat(num2) == 0) 
	    		{
	    			throw new UnsupportedOperationException("Can't divide by zero");
	    		}
	    		return Float.parseFloat(num1) / Float.parseFloat(num2);
	    	}
    	return 0;
    }
}

