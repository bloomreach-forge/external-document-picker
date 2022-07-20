<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="javax.persistence.*" %>
<%@ page import="org.onehippo.forge.exdocpicker.demo.jpa.model.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Informants</title>
</head>
<body>

<%
EntityManagerFactory factory = Persistence.createEntityManagerFactory("newsPU");
EntityManager entityManager = factory.createEntityManager();

TypedQuery<Informant> query = entityManager.createQuery("SELECT i FROM Informant i", Informant.class);
List<Informant> resultList = query.getResultList();
pageContext.setAttribute("resultList", resultList);

entityManager.close();
factory.close();
%>

<h1>Informants</h1>

<table border="1">
  <caption>Informant</caption>
  <thead>
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Description</th>
      <th>Photo</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="informant" items="${resultList}">
      <tr>
        <td><c:out value="${informant.id}" /></td>
        <td><c:out value="${informant.name}" /></td>
        <td><c:out value="${informant.description}" /></td>
        <td><img src="${informant.photo}" alt="Informant Photo" /></td>
      </tr>
    </c:forEach>
  </tbody>
</table>

</body>
</html>