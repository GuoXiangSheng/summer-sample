package cn.cerc.sample.services;

import cn.cerc.jbean.core.AbstractService;
import cn.cerc.jbean.core.DataValidateException;
import cn.cerc.jbean.core.IStatus;
import cn.cerc.jbean.core.ServiceException;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.mysql.SqlQuery;

public class SvrGoodsSave extends AbstractService {

	@Override
	public IStatus execute(DataSet headIn, DataSet headOut) throws ServiceException {
		Record head = headIn.getHead();
		DataValidateException.stopRun("参数错误", !head.hasValue("goodsName"));

		SqlQuery ds = new SqlQuery(this);
		ds.add("select goodsName,goodsDesc from %s", "t_goods");
		ds.setMaximum(1);
		ds.open();
		ds.append();
		ds.setField("goodsName", head.getSafeString("goodsName"));
		ds.setField("goodsDesc", head.getSafeString("goodsDesc"));
		ds.post();
		return success("成功");
	}
	
	@Override
	public boolean checkSecurity(IHandle handle) {
		return true;
	}

}
