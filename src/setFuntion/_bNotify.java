package setFuntion;

import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bNotify;
import jcx.util.convert;
import com.ysk.service.BaseService;

public class _bNotify extends bNotify{
	/**
	 * 取得簽核者的mail
	 * @param t
	 * @param vid
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Vector getmail(talk t,Vector vid) throws SQLException, Exception{
		Vector V2 = new Vector();
		// 查詢簽核者的email
		for (int i = 0; i < vid.size(); i++) {
			String sql = "select EMAIL from HRUSER where EMPID = '"
					+ convert.ToSql(vid.elementAt(i).toString()) + "' ";
			String r1[][] = t.queryFromPool(sql);
			if (r1.length == 0)
				continue;
			V2.addElement(r1[0][0].trim());
		}
		if (V2.size() == 0) return null;
		return V2;
	}
	/**
	 * 寄出mail
	 * @param V2
	 * @param flowService
	 * @param title
	 * @param content
	 */
	public void sendmail(Vector V2,Object flowService,String title,String content){
		String usr[] = (String[]) V2.toArray(new String[0]);
		String sendRS = ((BaseService) flowService).sendMailbccUTF8(usr, title, content,
				null, "", "text/plain");
		if (sendRS.trim().equals("")) {
			message("EMAIL已寄出通知");
		} else {
			message("EMAIL寄出失敗");
		}
	}
	@Override
	public void actionPerformed(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		
	}

}
