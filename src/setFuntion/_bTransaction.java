package setFuntion;

import jcx.jform.bTransaction;

public class _bTransaction extends bTransaction {
	/**
	 * �ˬd���O�_���ŭ�
	 * @param field
	 * @param field_name
	 * @return
	 */
	public boolean check_field(String[] field,String[] field_name) {
		boolean next = true;
		for (int i = 0; i < field.length; i++) {
			field[i] = getValue(field[i]);
			if(field[i].length()==0){
				message(field_name[i]+"���o����!");
				next = false;
				break;
			}
		}
		return next;
	}

	@Override
	public boolean action(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return false;
	}

}
