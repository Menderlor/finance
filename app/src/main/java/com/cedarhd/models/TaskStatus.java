package com.cedarhd.models;

/*最普通的枚举*/
public enum TaskStatus {
	重新启动(1), 完成(3), 暂停(2), 搁置(4);

	private int _value;

	private TaskStatus(int value) {
		_value = value;
	}

	public int value() {
		return _value;
	}

}
