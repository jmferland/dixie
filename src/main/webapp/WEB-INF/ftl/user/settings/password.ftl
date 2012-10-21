<#include "../../lib/core.ftl"/>
<#include "../../lib/web/form.ftl"/>
<@s.layout_render name="/WEB-INF/ftl/user/settings/settings_layout.ftl"
    title="Change Password">

    <@s.layout_component name="body">
        <h3>Change Password</h3>

        <@s.form beanclass=actionBean.beanclass>
            <@FormRow>
                <@s.label for="oldPassword"/>
                <@s.password name="oldPassword" id="oldPassword"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="newPassword"/>
                <@s.password name="newPassword" id="newPassword"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="newPassword2"/>
                <@s.password name="newPassword2" id="newPassword2"/>
            </@FormRow>
            <@s.submit name="update" value="Save"/>
            <input type="reset" name="Reset"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>