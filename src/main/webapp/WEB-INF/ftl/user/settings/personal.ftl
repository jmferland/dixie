<#include "../../lib/core.ftl"/>
<#include "../../lib/web/form.ftl"/>
<@s.layout_render name="/WEB-INF/ftl/user/settings/settings_layout.ftl"
    title="Personal Information">

    <@s.layout_component name="body">
        <h3>Personal Information</h3>

        <@s.form beanclass=actionBean.beanclass>
            <@s.errors/>
            <@FormRow>
                <@s.label for="firstName"/>
                <@s.text name="firstName" id="firstName"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="lastName"/>
                <@s.text name="lastName" id="lastName"/>
            </@FormRow>
            <@s.submit name="update" value="Save"/>
            <input type="reset" name="Reset"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>