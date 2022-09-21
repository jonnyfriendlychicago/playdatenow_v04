<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="/WEB-INF/include/head.jsp" />
<jsp:include page="/WEB-INF/include/bodyDesign.jsp" />
<jsp:include page="/WEB-INF/include/headerLoginReg.jsp" />
<jsp:include page="/WEB-INF/include/pageLayoutTop.jsp" />

<c:if test="${validationErrorMsg != null}">
	<div class="alert alert-danger" role="alert">
		${validationErrorMsg}</div>
</c:if>
<div id="registerCard"
	class="card p-3 d-md-flex justify-content-center align-items-center">
	<h4>Register</h4>
	<form:form action='/register' method='post' modelAttribute='newUser'>

		<div class="form-floating mb-3">
			<form:input path="firstName" type="text" class="form-control"
				id="firstName" placeholder="firstName" />
			<form:label path="firstName" for="firstName">firstName</form:label>
			<p class="text-danger">
				<form:errors path="firstName" />
		</div>

		<div class="form-floating mb-3">
			<form:input path="lastName" type="text" class="form-control"
				id="lastName" placeholder="lastName" />
			<form:label path="lastName" for="lastName">lastName</form:label>
			<p class="text-danger">
				<form:errors path="lastName" />
		</div>

		<div class="form-floating mb-3">
			<form:input path="userName" type="text" class="form-control"
				id="userName" placeholder="userName" />
			<form:label path="userName" for="userName">userName</form:label>
			<p class="text-danger">
				<form:errors path="userName" />
		</div>

		<div class="form-floating mb-3">
			<form:input path="email" type="email" class="form-control"
				id="floatingEmail" placeholder="name@example.com" />
			<form:label path="email" for="floatingEmail">Email</form:label>
			<p class="text-danger">
				<form:errors path="email" />
		</div>

		<div class="form-floating mb-3">
			<form:input path="password" type="password" class="form-control"
				id="floatingPassword" placeholder="Password" />
			<form:label path="password" for="floatingPassword">Password</form:label>
			<p class="text-danger">
				<form:errors path="password" />
		</div>


		<div class="form-floating mb-3">
			<form:input path="confirm" type="password" class="form-control"
				id="floatingConfirm" placeholder="Password" />
			<form:label path="confirm" for="floatingConfirm">Confirm Password</form:label>
			<p class="text-danger">
				<form:errors path="confirm" />
		</div>

		<button type="submit" class="w-100 btn btn-primary">Register</button>

		<p>
			Already have account? <a href="/" class="link-primary">Login</a>
	</form:form>
</div>
<!-- end registerCard -->
<jsp:include page="/WEB-INF/include/pageLayoutBottom.jsp" />