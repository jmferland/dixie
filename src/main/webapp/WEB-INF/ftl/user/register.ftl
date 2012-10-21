<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>

<@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
    title="Login / Register">

    <@s.layout_component name="messages"/>

    <@s.layout_component name="body">
        <@s.form beanclass="dixie.web.action.RegisterActionBean">
            <@s.errors/>
            <@FormRow>
                <@s.label for="register.username"/>
                <@s.text name="user.username" id="register.username"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="password"/>
                <@s.password name="password" id="password"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="password2"/>
                <@s.password name="password2" id="password2"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="register.email"/>
                <@s.text name="user.email" id="register.email"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="register.birthDate"/>
                <@s.select name="birthMonth">
                    <@s.option value="" label="month"/>
                    <#list Calendar.months() as month>
                        <@s.option value=month_index label=month/>
                    </#list>
                </@s.select>
                <@s.select name="birthDay">
                    <@s.option value="" label="day"/>
                    <#list Calendar.days() as day>
                        <@s.option value=day label=day/>
                    </#list>
                </@s.select>
                <@s.select name="birthYear">
                    <@s.option value="" label="year"/>
                    <#list Calendar.years() as year>
                        <@s.option value=year label=year/>
                    </#list>
                </@s.select>
            </@FormRow>
            <@include beanclass="dixie.web.action.AskCaptchaActionBean"/>
            <@s.hidden name="referrer" value=actionBean.referer/>
            <@s.submit name="register" value="Register"/>
        </@s.form>

        <@s.form beanclass="dixie.web.action.LoginActionBean">
            <@s.errors/>
            <@FormRow>
                <@s.label for="username"/>
                <@s.text name="username" id="username"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="password"/>
                <@s.password name="password" id="password"/>
            </@FormRow>
            <@s.hidden name="referrer" value=actionBean.referer/>
            <@s.submit name="login" value="Login"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>