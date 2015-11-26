package setFuntion;

import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bRule;

public class _bRule extends bRule{
	
	public String[][] getchief(talk t,String EMPID) throws SQLException, Exception{
		String sql = "select b.dep_chief,b.parent_no from hruser a,hruser_dept_bas b where a.dept_no=b.dep_no and a.empid='"+EMPID+"'";
		String chief[][] = t.queryFromPool(sql);
		return chief;		
	}
	
	public String[][] getparent_chief(talk t,String parent_no) throws SQLException, Exception{
		String sql= "select dep_chief from hruser_dept_bas where dep_no='"+parent_no+"'";
		String p_chief[][] = t.queryFromPool(sql);
		return p_chief;
	}

	@Override
	public Vector getIDs(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
