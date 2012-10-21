<#include "../lib/web/form.ftl"/>
<@s.form beanclass="dixie.web.action.AskCaptchaActionBean" partial=true>
    <@FormRow>
        <@s.url var="imageUrl" beanclass="dixie.web.action.ViewCaptchaJpgActionBean">
            <@s.param name="key" value=actionBean.captcha.uuidString/>
        </@s.url>
        <@s.label for="captcha"/>
        <#-- NOTE: the name-s "captcha" refers to something completely different
             than the value-s "captcha." The former is to supply a field name to
             a (different) receiving ActionBean. The latter is to read from the
             current ("AskCaptchaActionBean") ActionBean. -->
        <#-- Do not use Stripes "text" tag since we never want to remember this
             value. -->
        <input type="text" name="captcha.answer" id="captcha" value=""/>
        <img src="${imageUrl}"/>
        <#-- Cannot use Stripes tag since it uses the value as a default and we
          -- need the value to always be exactly what we say. -->

        <input type="hidden" name="captcha.uuid" value="${actionBean.captcha.uuidString}"/>
    </@FormRow>
</@s.form>