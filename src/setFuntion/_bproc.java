package setFuntion;

import java.sql.SQLException;

import jcx.db.talk;
import jcx.jform.bproc;
import jcx.jform.cLabel;

public class _bproc extends bproc {
	/**
	 * �]�w��ƪ��Ҧ��������
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
		// �B�zfield ex.select xx,xx,xx
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		// �}�lselect
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " a where a.PNO='" + table_PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		// �B�zfield ��^��
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		// setValue�Ҧ����
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], ret[0][i].trim());
		}
		return ret;
	}

	/**
	 * �`�Ϊ��dEMPID �m�W�A���������]�w
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
	 * �]�w�q���d�ߪ����
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
