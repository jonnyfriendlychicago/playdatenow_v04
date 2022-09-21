<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


  
<nav class="navbar navbar-expand-sm navbar-dark bg-dark" aria-label="Third navbar example">
    <div class="container-fluid">
      <!-- <a class="navbar-brand" href="#">PlayDateNOW</a> -->
      
      <a class="navbar-brand" href="/playdate">
      	<!-- <img src="https://github.com/mdo.png" alt="mdo" width="32" height="32" class="rounded-circle"> -->
      	<!-- <img src="/img/pdnWhiteLogoNoBg.png" alt="mdo" width="32" height="32" class="rounded-circle"> -->
      	<img src="/img/pdnWhiteLogoNoBg.png" alt="mdo" height="32" >
      </a>
      
      
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarsExample03" aria-controls="navbarsExample03" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarsExample03">
        <ul class="navbar-nav me-auto mb-2 mb-sm-0">
         <!-- 
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="#">Home</a>
          </li>
           -->
          <li class="nav-item">
            <a class="nav-link" href="/playdate">Dashboard</a>
          </li>
         <li class="nav-item">
            <a class="nav-link" href="/profile">Members</a>
          </li>
         <!--  <li class="nav-item">
            <a class="nav-link disabled">Disabled</a>
          </li> -->
          
<!--           <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" aria-expanded="false">Dropdown</a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">Action</a></li>
              <li><a class="dropdown-item" href="#">Another action</a></li>
              <li><a class="dropdown-item" href="#">Something else here</a></li>
            </ul>
          </li> -->
          
        </ul>
<!-- 
        <form role="search">
          <input class="form-control" type="search" placeholder="Search" aria-label="Search">
        </form>
 -->
<!--          <div class="btn-group">
		  <button type="button" class="btn btn-secondary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
		    Right-aligned menu
		  </button>
		  <ul class="dropdown-menu dropdown-menu-end">
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		  </ul>
		</div> -->
		
<!-- 		<div class="btn-group">
		  <button type="button" class="btn btn-secondary dropdown-toggle" data-bs-toggle="dropdown" data-bs-display="static" aria-expanded="false">
		  <button type="button" class="btn btn-link dropdown-toggle" data-bs-toggle="dropdown" data-bs-display="static" aria-expanded="false">
		    Left-aligned, right-aligned lg
		  </button>
		  <ul class="dropdown-menu dropdown-menu-lg-end">
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		    <li><a class="dropdown-item" href="#">Menu item</a></li>
		  </ul>
		</div> -->
		
		   <div class="nav-item dropdown ml-0">
            <a class="nav-link dropdown-toggle text-light" href="#" data-bs-toggle="dropdown" aria-expanded="false">${authUser.userName}</a>
            <ul class="dropdown-menu dropdown-menu-lg-end">
              <li><a class="dropdown-item" href="/profile/${authUser.id}">Profile</a></li>
				<li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="/logout">Log out</a></li>
            </ul>
          </div>
		        
      </div>
    </div>
  </nav>