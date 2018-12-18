import java.util.*;

public class SemanticCheckVisitor implements ParserVisitor
{
  String scope = "Program";
  HashMap<String,HashMap<String,STC>> ST = new HashMap<>();
  int numErrors = 0;

  public Object visit(SimpleNode node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTProgram node, Object data)
  {
    ST.put(scope,new HashMap<String,STC>());

    node.childrenAccept(this,data);
    
    System.out.println("--------Symbol Table---------\n");
    Set STKeys = ST.keySet();
    Iterator iterator = STKeys.iterator();
    while(iterator.hasNext())
    {
      String temp = (String) iterator.next();
      System.out.println("Scope: "+ temp);

      Set STKeys2 = ST.get(temp).keySet();
      Iterator iterator2 = STKeys2.iterator();
      while(iterator2.hasNext())
      {
        String temp2 = (String) iterator2.next();
        STC temp3 = ST.get(temp).get(temp2);

        System.out.print("Name " + temp2 + " "); 
        System.out.print(" DataType: " + temp3.dataType);
        System.out.print(" Type: " + temp3.type.image);
        System.out.print(" Values: " + temp3.values); 
        System.out.println();
      } 
    }

    if(numErrors > 0)
    {
      System.out.println("Errors: " + numErrors);
    }

    return null;
  }

  public Object visit(ASTDeclList node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTVarDecl node, Object data)
  {
    HashMap<String,STC> tempScope = ST.get(scope);
    if(tempScope == null)
    {
      tempScope = new HashMap<String,STC>();
    }
    Token name = (Token) node.jjtGetChild(0).jjtAccept(this,null);
    
    STC id = tempScope.get(name.image);
    if (id == null) {
      Token type = (Token) node.jjtGetChild(1).jjtAccept(this, null);
      STC stc = new STC(name, type, scope, DataType.Variable);
      tempScope.put(name.image, stc);
    } else {
      System.out.println("Variable " + name.image + " already declared in scope " + scope);
      numErrors++;
    }
    ST.put(scope, tempScope);

    return null;
  }
  public Object visit(ASTConstDecl node, Object data)
  {
    HashMap<String, STC> tempScope = ST.get(scope);
    if (tempScope == null) {
      tempScope = new HashMap<String, STC>();
    }
    Token name = (Token) node.jjtGetChild(0).jjtAccept(this, null);

    STC id = tempScope.get(name.image);
    if (id == null) {
      Token type = (Token) node.jjtGetChild(1).jjtAccept(this, null);
      STC stc = new STC(name, type, scope, DataType.Constant);
      tempScope.put(name.image, stc);

      Token value = (Token) node.jjtGetChild(2).jjtAccept(this, null);      
      if (type.image.equals("integer") && !value.image.matches("-?\\d+")) {
        System.out.println("Cannot set value " + value.image + " to constant " + name.image + " It's not a type: " + type.image );
        numErrors++;
      } else if (type.image.equals("boolean") && !Boolean.parseBoolean(value.image)) {
        System.out.println("Cannot set value " + value.image + " to constant " + name.image + " It's not a type: " + type.image );
        numErrors++;
      } else {
        stc.addValue(name.image, value);
      }
    } else {
      System.out.println("Variable " + name.image + " already declared in scope " + scope);
      numErrors++;
    }
    ST.put(scope, tempScope);

    return null;
  }
  public Object visit(ASTFunctionList node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTFunction node, Object data)
  {
    HashMap<String, STC> tempScope = ST.get(scope);
    if (tempScope == null) {
      tempScope = new HashMap<String, STC>();
    }
    Token type = (Token) node.jjtGetChild(0).jjtAccept(this, null);
    Token name = (Token) node.jjtGetChild(1).jjtAccept(this, null);
    STC stc = new STC(name, type, scope, DataType.Function);

    STC id = tempScope.get(name.image);
    if (id == null) {
      tempScope.put(name.image, stc);
      ST.put(scope, tempScope);
    } else {
      System.out.println("Function " + name.image + " already declared in scope " + scope);
      numErrors++;
    }

    node.jjtGetChild(2).jjtAccept(this, null);
    node.jjtGetChild(3).jjtAccept(this, null);
    node.jjtGetChild(4).jjtAccept(this, null);
    node.jjtGetChild(5).jjtAccept(this, null);
    return null;
  }
  public Object visit(ASTFunctionReturn node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTType node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTParameterList node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTNempParameterList node, Object data)
  {
    HashMap<String, STC> tempScope = ST.get(scope);
    if (tempScope == null) {
      tempScope = new HashMap<String, STC>();
    }

    Token type = (Token) node.jjtGetChild(1).jjtAccept(this, null);
    Token name = (Token) node.jjtGetChild(0).jjtAccept(this, null);
    STC stc = new STC(name, type, scope, DataType.ParamVariable);

    tempScope.put(name.image, stc);

    ST.put(scope, tempScope);

    return null;
  }
  public Object visit(ASTMain node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTAssignment node, Object data)
  {
    Token name = (Token) node.jjtGetChild(0).jjtAccept(this, null);
    Token value = (Token) node.jjtGetChild(1).jjtAccept(this, null);
    System.out.println(name + " " + value);
    node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTStatementBlock node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }

  public Object visit(ASTFunctionCall node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTStatement node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTPlus node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTMinus node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTOr node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTAnd node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTEqual node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTNotEqual node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTLessThan node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTGreaterThan node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTGreater node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTGreaterOrEqual node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTArgList node, Object data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  public Object visit(ASTNumber node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTBoolean node, Object data)
  {
    return node.jjtGetValue();
  }
  public Object visit(ASTIdentifier node, Object data)
  {
    return node.jjtGetValue();
  }
}