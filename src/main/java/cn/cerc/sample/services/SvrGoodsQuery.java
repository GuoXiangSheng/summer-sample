package cn.cerc.sample.services;

import cn.cerc.jbean.core.AbstractService;
import cn.cerc.jbean.core.IStatus;
import cn.cerc.jbean.core.ServiceException;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.mysql.SqlQuery;

public class SvrGoodsQuery extends AbstractService {

	@Override
	public IStatus execute(DataSet headIn, DataSet headOut) throws ServiceException {

		SqlQuery ds = new SqlQuery(this);
		ds.add("select goodsName,goodsDesc from %s", "t_goods");
		ds.open();
		headOut.appendDataSet(ds);
		return success("成功");
	}
	
	@Override
	public boolean checkSecurity(IHandle handle) {
		return true;
	}

}
