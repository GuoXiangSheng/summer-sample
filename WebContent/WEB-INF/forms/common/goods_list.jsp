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
		<table>
			<tr>
				<td>商品名称</td>
				<td>商品描述</td>
			</tr>
			<c:forEach var="goods" items="${goodsList }">
				<tr>
				<td>${goods.items.goodsName }</td>
				<td>${goods.items.goodsDesc }</td>
			</tr>
			</c:forEach>
		</table>
		</div>
	</div>
</body>
</html>