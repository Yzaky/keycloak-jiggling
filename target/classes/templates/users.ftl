<#import "/spring.ftl" as spring>

<html>

<h1> Hello ${principal.getName()} </h1>

<ul>

	<#list users as user>

		<li> ID: ${user.getID()}       Name: ${user.getName()} ${user.getLast_name()} </li>

	</#list>

</ul>

<br>

<a href="/logout">Log out </a>

</html>