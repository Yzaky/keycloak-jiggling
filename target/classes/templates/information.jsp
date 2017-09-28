<#import "/spring.ftl" as spring>

<html>

<h1> Hello ${principal.getName()}</h1>

<br>

${information}

<br>

<a href="/logout">Log out </a>

</html>