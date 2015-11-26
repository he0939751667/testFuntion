package setFuntion;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bProcFlow;
import jcx.util.convert;

import com.ysk.service.BaseService;

public class _bProcFlow extends bProcFlow {
	/**
	 * ��s��檺field���
	 * 
	 * @param tablename
	 * @param field
	 * @param PKfield
	 * @return
	 */
	public String UPDATE_MOVE_TYPE(String tablename, String[] field,String[] field_data, String PNO) {
		talk t = getTalk();
		StringBuffer sb = new StringBuffer();
		sb.append("update " + tablename + " set ");
		for (int i = 0; i < field_data.length - 1; i++) {
			field_data[i] = field_data[i]+"',";
		}
		for (int i = 0; i < field_data.length; i++) {
			sb.append(field[i] + "='" + field_data[i]);
		}
		sb.append("' where PNO='"+ convert.ToSql(PNO) + "'");
		addToTransaction(sb.toString());
		message("��s����");
		return null;
	}
	/**
	 * �s�W�@����ƨ��ƪ�
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @return
	 */
	public void INSERT_DATA(String tablename, String[] field,
			String[] field_data) {
		talk t = getTalk();
		for(int i=0;i<field.length-1;i++){
			field[i] = field[i]+",";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+tablename+" (");
		for(int i=0;i<field.length;i++){
			sb.append(field[i]);
		}
		sb.append(" ) values (");
		for(int i=0;i<field_data.length-1;i++){
			sb.append("'"+field_data[i]+"',");
		}
		sb.append("'"+field_data[field_data.length-1]+"')");
		addToTransaction(sb.toString());
		message("�s�W����");
	}
	/**
	 * �o�email���Ҧ�ñ�֪�
	 * @param t
	 * @param get_tot_user
	 * @param title
	 * @param content
	 * @param flowService
	 * @param EMPID
	 * @throws SQLException
	 * @throws Exception
	 */
	public void sendallmail(talk t,String[][] get_tot_user,String title,String content,Object flowService) throws SQLException, Exception{
		Vector V2 = new Vector();
		String auser[] = new String[get_tot_user.length];
		for (int i = 0; i < auser.length; i++) {
			auser[i] = get_tot_user[i][1].trim();
		}
		// �B�z�������ƪ�ñ�֪�
		HashSet set = new HashSet();
		set.addAll(Arrays.asList(auser));
		String tot_user[] = (String[]) set.toArray(new String[0]);

		for (int i = 0; i < tot_user.length; i++) {
			String sql = "select EMAIL from HRUSER where EMPID = '"
					+ tot_user[i] + "' ";
			String r1[][] = t.queryFromPool(sql);
			if (r1.length == 0)
				continue;
			V2.addElement(r1[0][0].trim());
			// mail�榡
			String usr[] = (String[]) V2.toArray(new String[0]);
			String sendRS = ((BaseService) flowService).sendMailbccUTF8(usr, title,
					content, null, "", "text/plain");
			if (sendRS.trim().equals("")) {
				message("EMAIL�w�H�X�q��");
			} else {
				message("EMAIL�H�X����");
			}
		}
	}
	@Override
	public boolean action(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return false;
	}

}
