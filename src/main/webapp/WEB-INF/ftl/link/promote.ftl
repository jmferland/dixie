<#include "../lib/core.ftl"/>

<@compress single_line=true>
<h1>Promote link</h1>

<#if actionBean.link?exists>
    <p><a href="${actionBean.link.url}">${actionBean.link.title}</a></p>
</#if>

<@s.errors/>

<#if !actionBean.hasErrors>
    <p></p>
</#if>

</@compress>