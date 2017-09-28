<#import "/spring.ftl" as spring>

<html>

<h1> Hello ${principal.getName()}</h1>

<ul>

	<#list products as product>

		<li> ${product.getName()} </li>

	</#list>

</ul>

<br>

<a href="/logout">Log out </a>

</html>