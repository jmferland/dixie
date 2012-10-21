<#include "../lib/web/form.ftl"/>

<@compress single_line=true>
<h1>Demote link</h1>

<#if actionBean.link?exists>
    <p><a href="${actionBean.link.url}">${actionBean.link.title}</a></p>
</#if>

<@s.form beanclass=actionBean.beanclass>
    <@s.errors/>
    <@FormRow>
        <@s.label for="demotionReason"/>
        <@s.select name="reason" id="demotionReason">
            <@s.options_enumeration enum="dixie.lang.DemotionReason"/>
        </@s.select>
    </@FormRow>
    <@s.submit name="demote" value="Submit"/>
    <@s.param name="link" value=actionBean.link/>
</@s.form>

<#if !actionBean.hasErrors>
    <p>You have successfully demoted this link. Specify a new reason or return to the previous page (using your browser's back button).</p>
</#if>

</@compress>