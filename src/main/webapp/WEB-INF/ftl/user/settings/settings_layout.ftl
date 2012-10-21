<#include "../../lib/core.ftl"/>
<@s.layout_definition>
    <@s.layout_render name="/WEB-INF/ftl/user/user_layout.ftl"
        title="Settings / " + title
        user=actionBean.loggedInUser>

        <@s.layout_component name="header">
            <h3>Options</h3>
            <ul>
                <#list
                    [
                        ["personal info",   "dixie.web.action.user.settings.PersonalInfoActionBean"],
                        ["email settings",  "dixie.web.action.user.settings.EmailSettingsActionBean"],
                        ["change password", "dixie.web.action.user.settings.ChangePasswordActionBean"]
                    ] as link>

                    <#if link[1] == actionBean.beanclass>
                        <li class="active">
                    <#else>
                        <li>
                    </#if>

                        <@s.link beanclass=link[1]>
                            ${link[0]}
                        </@s.link>
                    </li>
                </#list>
            </ul>
            ${header!}
        </@s.layout_component>

        <@s.layout_component name="body">
            ${body}
        </@s.layout_component>

    </@s.layout_render>
</@s.layout_definition>