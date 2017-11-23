package com.cedarhd.comparator;

import com.cedarhd.control.CheckBoxListViewItem;
import com.cedarhd.helpers.GB2Alpha;
import com.cedarhd.helpers.GB2Alpha.LetterType;

import java.util.Comparator;

public class CheckBoxListViewItemComparator implements
		Comparator<CheckBoxListViewItem> {

	GB2Alpha obj1 = new GB2Alpha();
	String Name1;
	String Name2;

	/**
	 * 比较器方法 先按名称守字母排序
	 */
	@Override
	public int compare(CheckBoxListViewItem o1, CheckBoxListViewItem o2) {
		Name1 = obj1.String2AlphaFirst(o1.getName(), LetterType.Uppercase);
		Name2 = obj1.String2AlphaFirst(o2.getName(), LetterType.Uppercase);

		return Name1.compareTo(Name2);

		// if(o1.getAge()> o2.getAge())
		// return 1;
		// else if(o1.getAge()==o2.getAge()){
		// if(o1.getId()>o2.getId())
		// return 1;
		// else if(o1.getId()==o2.getAge())
		// return 0;
		// else
		// return -1;
		// }
		// else
		// return -1;
	}

}
