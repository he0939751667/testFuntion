package setFuntion;

import hr.common;

import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.hproc;
import jcx.util.convert;

import com.ysk.service.BaseFlowService;
import com.ysk.service.BaseService;
import com.ysk.util.DateTimeUtil;
import com.ysk.field.Mail;

public class _hproc extends hproc {
	/**
	 * 查詢功能
	 * 
	 * @param tablename
	 * @param EMPID
	 * @param PNO
	 * @param MDATE
	 * @param MDATE1
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] queryFuntion(String tablename, String EMPID, String PNO,
			String MDATE, String MDATE1) throws SQLException, Exception {
		talk t = getTalk();
		String sql = "select PNO,EMPID,DATE,(select F_INP_STAT from "
				+ tablename + "_FLOWC where PNO=a.PNO), '簽核記錄', '查詢' from "
				+ tablename + " a where a.EMPID ='" + EMPID.trim() + "'";
		if (PNO.trim().length() != 0) {
			sql += "and a.PNO='" + PNO.trim() + "'";
		}
		if (MDATE.trim().length() != 0 && MDATE1.trim().length() != 0) {
			sql += " and a.DATE between '" + MDATE + "' and '" + MDATE1 + "'";
		} else if (MDATE.trim().length() == 0 && MDATE1.trim().length() != 0) {
			sql += " and a.DATE <= '" + MDATE1 + "'";
		} else if (MDATE.trim().length() != 0 && MDATE1.trim().length() == 0) {
			sql += " and a.DATE >= '" + MDATE + "'";
		}
		String s[][] = t.queryFromPool(sql);
		// 在table欄位裡的簽核狀態，判斷完後新增一段結案與未結案顯示的顏色
		for (int i = 0; i < s.length; i++) {
			if (s[i][3].trim().equals("結案"))
				s[i][3] = s[i][3].trim() + "<font color=blue>(已結案)</font>";
			else {
				Vector people = getApprovablePeople("穩定性取樣提醒異動單", "a.PNO='"
						+ s[i][0] + "'");
				StringBuffer sb = new StringBuffer();
				if (people != null) {
					if (people.size() != 0) {
						sb.append("(");
						for (int j = 0; j < people.size(); j++) {
							if (j != 0)
								sb.append(",");
							String id1 = (String) people.elementAt(j);
							String name1 = getName(id1);
							sb.append(name1 + ":" + id1);
						}
						sb.append(")");
					}
				}
				s[i][3] = s[i][3].trim() + "<font color=red>(未結案)"
						+ sb.toString() + "</font>";
			}
		}
		return s;
	}

	/**
	 * 常用的查EMPID 姓名，部門
	 * 
	 * @param EMPID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] user_info_view(String EMPID) throws SQLException,
			Exception {
		talk t = getTalk();
		String sql = "select hecname,DEP_NAME  from user_info_view where empid = '"
				+ EMPID.trim() + "'";
		String ret_empid[][] = t.queryFromPool(sql);
		setValue("EMPID", EMPID);
		setValue("EMP_NAME", ret_empid[0][0]);
		setValue("DEP_NAME", ret_empid[0][1]);
		return null;
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
	 * 新增資料至資料庫
	 * 
	 * @param tablename
	 * @param field
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] add_data(String tablename, String[] field,
			String[] field_data) throws SQLException, Exception {
		talk t = getTalk();
		// 處理field ex.insert into xxx (XXX,XXX,XXX...
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String MUSER = getUser();
		Vector SQL = new Vector();
		// 新增主檔及其流程
		String sql = "insert into " + tablename + " (";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += ") values (%PNO%,";
		for (int i = 0; i < field_data.length - 1; i++) {
			sql += "'" + convert.ToSql(field_data[i]) + "',";
		}
		sql += "'" + convert.ToSql(field_data[field_data.length - 1]) + "')";
		SQL.addElement(sql);
		String chief = "課主管";
		String now = getNow();
		String sc1 = "insert into " + tablename
				+ "_FLOWC (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc1 += "values (%PNO%,'" + chief + "','" + MUSER + "','" + now
				+ "','待處理')";
		String sc2 = "insert into " + tablename
				+ "_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc2 += "values (%PNO%,'待處理','" + MUSER + "','" + now + "','待處理')";
		String now1 = DateTimeUtil.getApproveAddSeconds(1);
		String sc3 = "insert into " + tablename
				+ "_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc3 += "values (%PNO%,'" + chief + "','" + MUSER + "','" + now1 + "','"
				+ chief + "')";

		SQL.addElement(sc1);
		SQL.addElement(sc2);
		SQL.addElement(sc3);
		// 處理單號
		while (true) {
			String strNewNo = getToday("YYYYmmdd");
			String strNewNo1 = "001";
			sql = "select max(PNO) from " + tablename + " where  PNO like '"
					+ strNewNo + "%' ";
			String s[][] = getTalk().queryFromPool(sql);
			try {
				int d = Integer.parseInt(s[0][0].trim().substring(8, 11));
				d = d + 1001;
				strNewNo1 = "" + d;
				strNewNo1 = strNewNo1.trim().substring(1);
			} catch (Exception e) {
				strNewNo1 = "001";
			}
			String PNO = strNewNo + strNewNo1.trim();
			String se[] = new String[SQL.size()];
			for (int i = 0; i < SQL.size(); i++) {
				String sqle = SQL.elementAt(i).toString();
				sqle = convert.replace(sqle.trim(), "%PNO%", PNO);
				se[i] = sqle.trim();
			}
			t.execFromPool(se);
			message("資料異動完成");
			return null;
		}
	}

	/**
	 * 常用的使用PNO去查詢一些表單的資料並設定field資料
	 * 
	 * @param talbename
	 * @param field
	 * @param PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] selectfield(String talbename, String[] field, String PNO)
			throws SQLException, Exception {
		talk t = getTalk();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + talbename + " where PNO='" + PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], ret[0][i]);
		}
		return ret;
	}

	/**
	 * 查詢某張表單資料
	 * 
	 * @param talbename
	 * @param field
	 * @param PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] selectfromwhere(String tablename, String[] field,
			String PNO) throws SQLException, Exception {
		talk t = getTalk();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " where PNO='" + PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		return null;
	}

	/**
	 * 防止人員欄位資料沒有填寫
	 * 
	 * @param field
	 * @param field_name
	 * @return
	 */
	public boolean forget_field(String[] field, String[] field_name) {
		Boolean forget = true;
		for (int i = 0; i < field.length; i++) {
			if (field[i].length() == 0) {
				message(field_name[i] + "不得為空!");
				forget = false;
				break;
			}
		}
		return forget;
	}

	/**
	 * 取得上級主管email
	 * 
	 * @param t
	 * @param EMPID
	 * @return
	 * @throws Throwable
	 */
	public String getBOS(talk t, String EMPID) throws Throwable {
		int level = 3;
		String MASTER[][] = t.queryFromPool(
				"select MASTERID from HRUSER where EMPID='"
						+ convert.ToSql(EMPID.trim()) + "'", 30);
		if (MASTER.length != 0) {
			if (!MASTER[0][0].trim().equals("")) {
				return MASTER[0][0].trim();
			}
		}
		Vector v = null;
		v = common.getBosses(t, EMPID.trim(), new Vector(), level);
		String id1 = "";
		for (int i = 0; i < v.size(); i++) {
			id1 = v.elementAt(i).toString().trim();
			if (id1.trim().equals(""))
				continue;
			if (id1.trim().equals(EMPID.trim()))
				continue;
			return id1.trim();
		}
		return id1;
	}

	/**
	 * 寄送email
	 * 
	 * @param t
	 * @param EMPID
	 * @param flowService
	 * @throws Throwable
	 */
	public void sendMail(talk t, String EMPID, Object flowService,String content)
			throws Throwable {

		String reEmpId = getBOS(t, EMPID);
		String[] usr = null;
		Vector vc = new Vector();
		String r2[][] = t
				.queryFromPool("select EMAIL from HRUSER where EMPID = '"
						+ convert.ToSql(reEmpId) + "' ");
		if (r2.length > 0) {
			for (int i = 0; i < r2.length; i++) {
				vc.addElement(r2[0][0].trim());
			}
		}
		usr = (String[]) vc.toArray(new String[0]);
		String HRSYS[][] = t.queryFromPool("select HRADDR from HRSYS");
		String title1 = "";
		if (HRSYS.length != 0) {
			if (HRSYS[0][0].trim().length() != 0) {
				String CP1[][] = t
						.queryFromPool("select CPNYID from HRUSER where EMPID='"
								+ EMPID + "'");
				String CPNYID = "";
				if (CP1.length != 0)
					CPNYID = CP1[0][0];
				int index1 = HRSYS[0][0].indexOf("," + CPNYID + "=");
				if (index1 == -1)
					if (HRSYS[0][0].startsWith(CPNYID + "="))
						index1 = 0;
				if (index1 != -1) {
					int index2 = HRSYS[0][0].indexOf(",", index1 + 1);
					if (index2 == -1)
						index2 = HRSYS[0][0].length();
					HRSYS[0][0] = HRSYS[0][0].substring(index1
							+ ("," + CPNYID + "=").length(), index2);
				}

				if (HRSYS[0][0].trim().toUpperCase().startsWith("HTTP"))
					title1 = "(" + HRSYS[0][0].trim() + ")";
				else
					title1 = "(http://" + HRSYS[0][0].trim() + ")";
			}
		}
		String M_DATE = getValue("DATE").trim();
		String DEP_NAME = getValue("DEP_NAME").trim();
		String empid = EMPID.trim();
		String name = getName(empid);
		String title = ((BaseFlowService) flowService).getMailSubject(empid,
				name);	
		if ((usr.length != 0) && (!content.trim().equals(""))) {
			try {
				String sendRS = ((BaseService) flowService).sendMailbccUTF8(
						usr, title, content, null, "", "text/plain");
			} catch (Exception e) {
				System.out.println("" + e);
			}
		}
		return;
	}

	/**
	 * 清空所有欄位
	 * 
	 * @param t
	 * @param field
	 */
	public void Clear_field(talk t, String[] field) {
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], "");
		}
	}

	/**
	 * 處理單號
	 * 
	 * @param t
	 * @param tablename
	 * @throws SQLException
	 * @throws Exception
	 */
	public void Process_PNO(talk t, String tablename) throws SQLException,
			Exception {

		try {
			String strNewNo = getToday("YYYYmmdd");
			String strNewNo1 = "001";
			String sql = "select max(PNO) from " + tablename
					+ " where  PNO like '" + strNewNo + "%' ";
			String s[][] = t.queryFromPool(sql);
			try {
				int d = Integer.parseInt(s[0][0].trim().substring(8, 11));
				d = d + 1001;
				strNewNo1 = "" + d;
				strNewNo1 = strNewNo1.trim().substring(1);
			} catch (Exception e) {
				strNewNo1 = "001";
			}
			String PNO = strNewNo + strNewNo1.trim();
			setValue("PNO", PNO);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 新增一筆資料到資料表
	 * 
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public void INSERT_DATA(String tablename, String[] field,
			String[] field_data) throws SQLException, Exception {
		talk t = getTalk();
		Vector SQL = new Vector();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "insert into " + tablename + " (PNO,";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " ) values (%PNO%,";
		for (int i = 0; i < field_data.length - 1; i++) {
			sql += "'" + field_data[i] + "',";
		}
		sql += "'" + field_data[field_data.length - 1] + "')";
		SQL.addElement(sql);
		// 處理單號
		String strNewNo = getToday("YYYYmmdd");
		String strNewNo1 = "001";
		sql = "select max(PNO) from " + tablename + " where  PNO like '"
				+ strNewNo + "%' ";
		String s[][] = t.queryFromPool(sql);
		try {
			int d = Integer.parseInt(s[0][0].trim().substring(8, 11));
			d = d + 1001;
			strNewNo1 = "" + d;
			strNewNo1 = strNewNo1.trim().substring(1);
		} catch (Exception e) {
			strNewNo1 = "001";
		}
		String PNO = strNewNo + strNewNo1.trim();
		String se[] = new String[SQL.size()];
		for (int i = 0; i < SQL.size(); i++) {
			String sqle = SQL.elementAt(i).toString();
			sqle = convert.replace(sqle.trim(), "%PNO%", PNO);
			se[i] = sqle.trim();
		}
		t.execFromPool(se);
		message("新增完成");
	}
	/**
	 * 依單號更新內容
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @throws SQLException
	 * @throws Exception
	 */
	public void update_data(String tablename,String[] field,String[] field_data,String PNO) throws SQLException, Exception{
		talk t = getTalk();
		String sql = "update "+tablename+" set ";
		for (int i = 0; i < field.length-1; i++) {
			sql+=field[i]+"='"+field_data[i]+"',";
		}
		sql+=field[field.length-1]+"='"+field_data[field.length-1]+"' where PNO='"+PNO+"'";
		t.execFromPool(sql);
		message("更新完成!");
	}
	@Override
	public String action(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
