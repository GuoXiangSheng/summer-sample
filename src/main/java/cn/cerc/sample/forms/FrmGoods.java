package cn.cerc.sample.forms;

import cn.cerc.jbean.client.LocalService;
import cn.cerc.jbean.form.IPage;
import cn.cerc.jmis.form.AbstractForm;
import cn.cerc.jmis.page.JsonPage;
import cn.cerc.jmis.page.JspPage;

public class FrmGoods extends AbstractForm {

	@Override
	public IPage execute() throws Exception {
		JspPage page = new JspPage(this, "common/goods.jsp");

		return page;
	}
public IPage query(){
	JspPage jspPage  = new JspPage(this, "common/goods_list.jsp");
	LocalService svr = new LocalService(this, "SvrGoodsQuery");
	if (!svr.exec()) {
		throw new RuntimeException(svr.getMessage());
	}
	jspPage.add("goodsList", svr.getDataOut().getRecords());
	return jspPage;
}
	
	
	public IPage addPage() {
		JspPage page = new JspPage(this, "common/add_goods.jsp");
		return page;
	}

	public IPage addSave() {
		JspPage page = new JspPage(this, "common/success.jsp");
		LocalService svr = new LocalService(this, "SvrGoodsSave");
		if (!svr.exec("goodsName", this.getRequest().getParameter("goodsName"), "goodsDesc", this.getRequest().getParameter("goodsDesc"))) {
			page.setJspFile("common/error.jsp");
			page.add("message", svr.getMessage());
		}
		return page;
	}

	@Override
	public boolean logon() {
		// TODO Auto-generated method stub
		return true;
	}

}
