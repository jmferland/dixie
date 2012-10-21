<#include "lib/core.ftl"/>
<#include "lib/web/form.ftl"/>

<@s.layout_definition>
<@compress single_line=true>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>

<head>
    <title>${title}</title>
</head>

<body>

<h1><a href="${homeUrl()}">dixie</a></h1>

<div>
    <#if actionBean.loggedInUser?exists>
        <#-- \todo hook up about URL (and write the ActionBean for it). -->
        <a href="${profileUrl(actionBean.loggedInUser)}">Profile</a>
        <a href="${logoutUrl()}">Logout</a>
    <#else>
        <a href="${registerUrl()}">Join</a>
        <#-- \todo hook up about URL (and write the ActionBean for it). -->
        <a href="">About</a>
        <a href="${loginUrl()}">Login</a>
    </#if>

    <@s.form beanclass="dixie.web.action.SearchLinksActionBean">
        <@s.text name="q"/>
    </@s.form>
</div>

<ul>
    <li><a href="${submitLinkUrl()}">Submit</a></li>
    <li><a href="${linksUrl()}">Links</a></li>
    <li><a href="">Tags</a></li>
    <li><a href="">Users</a></li>
</ul>

<@s.layout_component name="header"/>

<@s.layout_component name="messages">
    <@s.errors/>
    <@s.messages/>
</@s.layout_component>

<@s.layout_component name="body"/>

</body>

<div>
    <p>&copy; 2009 TODO</p>
</div>

</html>

</@compress>
</@s.layout_definition>