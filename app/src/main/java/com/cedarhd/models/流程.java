package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 流程 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8264461250063873536L;

	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;// 编号

	@DatabaseField
	public int ClassTypeId;// 流程分类编号

	@DatabaseField
	public String ClassTypeName;// 流程分类名称

	@DatabaseField
	/**表单数据编号*/
	public int FormDataId;//

	@DatabaseField
	public String Name;// 名称

	@DatabaseField
	public String Create;// 创建人

	@DatabaseField
	public String CreateName;// 创建人名称

	@DatabaseField
	public String NextStepAudit;// 下个步骤审核人

	@DatabaseField
	public String CraeteDate;// 创建时间

	@DatabaseField
	public String CurrentState;// 当前状态

	@DatabaseField
	public int NextStepNumber;// 下个步骤编号

	@DatabaseField
	public String NextStep;// 下个步骤

	@DatabaseField
	public String UpStepCompleteDate;// 上个步骤完成时间

	@DatabaseField
	public String EditeCell;// 可编写单元格

	@DatabaseField
	public String HiddenCell;// 隐藏单元格

	@DatabaseField
	public String Workflow;// 工作流标识

	@DatabaseField
	public String FormName;// 表单名称

	@DatabaseField
	public Boolean IsComplete;// 完成

	@DatabaseField
	public String UpdateTime;

	@DatabaseField
	public String Read;// 是否读过

	@DatabaseField
	public String isPhoneData;// 是否手机表单

	@DatabaseField
	public String 已读时间;

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getClassTypeId() {
		return ClassTypeId;
	}

	public void setClassTypeId(int classTypeId) {
		ClassTypeId = classTypeId;
	}

	public String getClassTypeName() {
		return ClassTypeName;
	}

	public void setClassTypeName(String classTypeName) {
		ClassTypeName = classTypeName;
	}

	public int getFormDataId() {
		return FormDataId;
	}

	public void setFormDataId(int formDataId) {
		FormDataId = formDataId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCreate() {
		return Create;
	}

	public void setCreate(String create) {
		Create = create;
	}

	public String getCreateName() {
		return CreateName;
	}

	public void setCreateName(String createName) {
		CreateName = createName;
	}

	public String getNextStepAudit() {
		return NextStepAudit;
	}

	public void setNextStepAudit(String nextStepAudit) {
		NextStepAudit = nextStepAudit;
	}

	public String getCraeteDate() {
		return CraeteDate;
	}

	public void setCraeteDate(String craeteDate) {
		CraeteDate = craeteDate;
	}

	public String getUpStepCompleteDate() {
		return UpStepCompleteDate;
	}

	public void setUpStepCompleteDate(String upStepCompleteDate) {
		UpStepCompleteDate = upStepCompleteDate;
	}

	public String getCurrentState() {
		return CurrentState;
	}

	public void setCurrentState(String currentState) {
		CurrentState = currentState;
	}

	public int getNextStepNumber() {
		return NextStepNumber;
	}

	public void setNextStepNumber(int nextStepNumber) {
		NextStepNumber = nextStepNumber;
	}

	public String getNextStep() {
		return NextStep;
	}

	public void setNextStep(String nextStep) {
		NextStep = nextStep;
	}

	public String getEditeCell() {
		return EditeCell;
	}

	public void setEditeCell(String editeCell) {
		EditeCell = editeCell;
	}

	public String getHiddenCell() {
		return HiddenCell;
	}

	public void setHiddenCell(String hiddenCell) {
		HiddenCell = hiddenCell;
	}

	public String getWorkflow() {
		return Workflow;
	}

	public void setWorkflow(String workflow) {
		Workflow = workflow;
	}

	public String getFormName() {
		return FormName;
	}

	public void setFormName(String formName) {
		FormName = formName;
	}

	public Boolean getIsComplete() {
		return IsComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		IsComplete = isComplete;
	}

	public String getIsPhoneData() {
		return isPhoneData;
	}

	public void setIsPhoneData(String isPhoneData) {
		this.isPhoneData = isPhoneData;
	}

}
