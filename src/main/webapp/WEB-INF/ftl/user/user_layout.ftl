<#include "../lib/core.ftl"/>
<#include "lib/user.ftl"/>

<@s.layout_definition>
    <@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
        title=title>

        <@s.layout_component name="header">

            <img src="${gravatarUrl(user, 80)}"/>

            <h2>${user.username}</h2>

            <p>Joined up with us on ${user.createdOn?date}.</p>

            <#assign userLinks =
                [
                    ["Profile",     ["dixie.web.action.user.ViewUserActionBean"]],
                    ["Submissions", ["dixie.web.action.user.SubmittedLinksActionBean",
                                     "dixie.web.action.user.SubmittedLinksTaggedActionBean"]],
                    ["Favorites",   ["dixie.web.action.user.FavoritedLinksActionBean",
                                     "dixie.web.action.user.FavoritedLinksTaggedActionBean"]],
                    ["Promoted",    ["dixie.web.action.user.PromotedLinksActionBean",
                                     "dixie.web.action.user.PromotedLinksTaggedActionBean"]],
                    ["Comments",    ["dixie.web.action.user.CommentsActionBean"]]
                ]
            />

            <#if actionBean.loggedInUser?exists &&
                 actionBean.loggedInUser.id == user.id>
                <#assign userLinks = userLinks + [["Settings", ["dixie.web.action.user.settings.PersonalInfoActionBean"]]]/>
            </#if>

            <ul>
                <#list userLinks as link>

                    <li
                    <#list link[1] as beanclass>
                        <#if beanclass == actionBean.beanclass>
 class="active"<#nt>
                        </#if>
                    </#list>
                    >
                        <@s.link beanclass=link[1][0]>
                            <#if link[1][0] != "dixie.web.action.user.settings.PersonalInfoActionBean">
                                <@s.param name="user" value=user.username/>
                            </#if>
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