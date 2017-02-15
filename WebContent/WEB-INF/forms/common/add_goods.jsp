<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>商品操作页</title>
</head>
<body>
	<div style="text-align: center; padding-top: 2em;">
		<div>
			<form action="FrmGoods.addSave" method="post">
				商品名称:<input type="text" name="goodsName" ><br/>
				商品描述:<input type="text" name="goodsDesc" ><br/>
				<input type="submit" >
			</form>
		</div>
	</div>
</body>
</html>