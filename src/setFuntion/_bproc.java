package setFuntion;

import java.sql.SQLException;

import jcx.db.talk;
import jcx.jform.bproc;
import jcx.jform.cLabel;

public class _bproc extends bproc {
	/**
	 * 設定資料表中所有的欄位資料
	 * @param tablename
	 * @param field
	 * @param table_PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] setDetail(String tablename, String[] field,
			String table_PNO) throws SQLException, Exception {
		talk t = getTalk();
		// 處理field ex.select xx,xx,xx
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		// 開始select
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " a where a.PNO='" + table_PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		// 處理field 改回來
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		// setValue所有欄位
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], ret[0][i].trim());
		}
		return ret;
	}

	/**
	 * 常用的查EMPID 姓名，部門但不設定
	 * 
	 * @param EMPID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] user_info_view2(String EMPID) throws SQLException,
			Exception {
		talk t = getTalk();
		String sql = "select hecname,DEP_NAME  from user_info_view where empid = '"
				+ EMPID.trim() + "'";
		String ret_empid[][] = t.queryFromPool(sql);
		return ret_empid;
	}
	/**
	 * 設定從表單查詢的資料
	 * @param t
	 * @param tablename
	 * @param field
	 * @param PNO
	 * @throws SQLException
	 * @throws Exception
	 */
	public void setfield(talk t,String tablename,String[] field,String PNO) throws SQLException, Exception{
		for(int i=0;i<field.length-1;i++){
			field[i] = field[i]+",";
		}
		String sql ="select ";
		for(int i=0;i<field.length;i++){
			sql+=field[i];
		}
		sql+=" from "+tablename+" where PNO='"+PNO+"'";
		String ret[][] = t.queryFromPool(sql);
		for(int i=0;i<field.length-1;i++){
			field[i] = field[i].substring(0,field[i].length()-1);
		}
		for(int i=0;i<field.length;i++){
			setValue(field[i],ret[0][i]);
		}
	}
	@Override
	public String getDefaultValue(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
