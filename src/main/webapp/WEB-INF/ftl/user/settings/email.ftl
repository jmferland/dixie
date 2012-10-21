<#include "../../lib/core.ftl"/>
<#include "../../lib/web/form.ftl"/>
<@s.layout_render name="/WEB-INF/ftl/user/settings/settings_layout.ftl"
    title="Email Settings">

    <@s.layout_component name="body">
        <h3>Change Email Address</h3>

        <@s.form beanclass=actionBean.beanclass>
            <@FormRow>
                <@s.label for="email"/>
                <@s.text name="email" id="email"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="password"/>
                <@s.password name="password" id="password"/>
            </@FormRow>
            <@s.submit name="update" value="Save"/>
            <input type="reset" name="Reset"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>