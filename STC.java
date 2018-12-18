
import java.util.HashMap;

public class STC extends Object
{
	Token name;
	Token type;
	DataType dataType; 
	String scope;
	HashMap<String,Object> values;

	public STC(Token id, Token itype, String iscope, DataType dt)
	{
		name = id;
		type = itype;
		scope = iscope;
		dataType = dt;
		values = new HashMap<String,Object>();
	}

	public STC(Token id, Token itype, DataType dt)
	{
		name = id;
		type = itype;
		dataType = dt;
		values = new HashMap<String,Object>();
	}

	public void addValue(String name, Object value)
	{
		values.put(name,value);
	}
}