package cn.cerc.sample.common;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.core.AbstractHandle;
import cn.cerc.jbean.core.Application;
import cn.cerc.jbean.core.CustomHandle;
import cn.cerc.jbean.core.IPassport;
import cn.cerc.jbean.other.SystemTable;
import cn.cerc.jbean.rds.PassportRecord;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.mysql.SqlQuery;
import cn.cerc.jmis.core.MenuData;

public class Passport extends AbstractHandle implements IPassport {

	// 是否激活
	private boolean active;
	private String usercode;
	private int userEnabled;
	private String corpType;
	private String roleCode;
	private boolean superUser;
	private Map<String, Map<String, Boolean>> items = new HashMap<>();

	@Override
	public void setHandle(IHandle handle) {
		super.setHandle(handle);
		if (handle != null) {
			this.usercode = handle.getUserCode();
			if (usercode == null || usercode.equals("")) {
				this.active = false;
				return;
			}
			SqlQuery ds = new SqlQuery(handle);

			ds.setCommandText(String.format(
					"select a.Code_,a.Enabled_,a.Name_,a.SuperUser_,a.DiyRole_,a.RoleCode_,"
							+ "oi.corpType_ from %s a inner join %s oi on a.CorpNo_=oi.CorpNo_ where a.Code_='%s'",
					SystemTable.get(SystemTable.getUserInfo), SystemTable.get(SystemTable.getBookInfo), usercode));
			ds.open();
			if (ds.eof())
				throw new RuntimeException(String.format("用户代码 %s 不存在!", usercode));
			Record record = ds.getCurrent();
			if (record.getBoolean("DiyRole_"))
				// 如果为自定义角色
				this.roleCode = record.getString("Code_");
			else
				this.roleCode = record.getString("RoleCode_");
			this.corpType = record.getInt("Type_") + ",";
			this.userEnabled = record.getInt("Enabled_");
			this.superUser = record.getBoolean("SuperUser_");
			this.roleCode = record.getString("RoleCode_");
			// 初始化完成
			active = true;
		}
	}

	@Override
	public boolean passProc(String versions, String procCode) {
		if (versions != null && !"".equals(versions)) {
			if (corpType == null)
				return false;
			if (versions.indexOf(this.corpType) == -1)
				return false;
		}

		if (procCode != null && procCode.equals("all.user.pass"))
			return true;

		if (!this.active)
			return false;

		if (userEnabled == 0)
			throw new RuntimeException(String.format("用户代码 %s 没有启用或被停用!", this.usercode));

		if (this.superUser)
			return true;

		if (procCode == null)
			return true;

		// 检查权限表中是否有授权
		if (items.size() == 0) {
			// 一次性从表中取出后缓存
			SqlQuery ds = new SqlQuery(handle);

			ds.setCommandText(
					String.format("SELECT procCode_,execute_,append_,modify_,delete_ FROM %s WHERE roleCode_='%s'",
							SystemTable.getRoleAccess, roleCode));
			ds.open();
			if (ds.eof()) {
				// 若没有数据，就随便存一个
				Map<String, Boolean> item = new HashMap<>();
				items.put("null", item);
			}
			while (!ds.eof()) {
				Record record = ds.getCurrent();
				Map<String, Boolean> item = new HashMap<>();
				item.put("Execute_", record.getBoolean("execute_"));
				item.put("Append_", record.getBoolean("append_"));
				item.put("Modify_", record.getBoolean("modify_"));
				item.put("Delete_", record.getBoolean("delete_"));
				items.put(record.getString("procCode_"), item);
				ds.next();
			}

		}

		// 从缓存中读取
		boolean result = false;
		Map<String, Boolean> record = items.get(procCode);
		if (record != null) {
			Boolean item1 = record.get("Execute_");
			if (item1 != null) {
				result = item1;
			}
		}
		return result;
	}

	/**
	 * 验证动作
	 */
	@Override
	public boolean passAction(String procCode, String action) {
		CustomHandle sess = (CustomHandle) handle.getProperty(null);
		String RoleCode = (String) sess.getProperty(Application.roleCode);
		if (RoleCode == null || cn.cerc.jdb.other.utils.trim(RoleCode).equals(""))
			throw new RuntimeException("权限代码不允许为空！");

		PassportRecord pass = getRecord(procCode);
		switch (action) {
		case "Execute":
			return pass.isExecute();
		case "Print":
			return pass.isPrint();
		case "Output":
			return pass.isOutput();
		case "Append":
			return pass.isAppend();
		case "Modify":
			return pass.isModify();
		case "Delete":
			return pass.isDelete();
		case "Final":
			return pass.isFinish();
		case "Cancel":
			return pass.isCancel();
		case "Recycle":
			return pass.isRecycle();
		default:
			return false;
		}
	}

	@Override
	public PassportRecord getRecord(String procCode) {
		PassportRecord result = new PassportRecord();
		SqlQuery ds = new SqlQuery(handle);

		CustomHandle sess = (CustomHandle) handle.getProperty(null);
		String roleCode = (String) sess.getProperty(Application.roleCode);
		ds.add("select execute_,append_,modify_ ,roleCode_ from %s ", SystemTable.getRoleAccess);
		ds.add("where roleCode_= '%s' and procCode_= '%s'", roleCode, procCode);
		ds.open();
		if (ds.eof())
			return result;

		result.setExecute(ds.getBoolean("execute_"));
		result.setAppend(ds.getBoolean("append_"));
		result.setModify(ds.getBoolean("modify_"));
		result.setDelete(ds.getBoolean("delete_"));
		return result;

	}

	/**
	 * 验证菜单
	 */
	@Override
	public boolean passsMenu(String menuCode) {
		MenuData data = MenuFactory.get(menuCode);
		if (data == null)
			throw new RuntimeException(String.format("菜单代码 %s 不存在!", menuCode));
		PassportRecord pass = getRecord(data.getProccode());
		return pass.isExecute();
	}

}
