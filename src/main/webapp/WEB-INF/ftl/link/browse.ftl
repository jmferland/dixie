<#include "../lib/web/form.ftl"/>
<#include "../user/lib/user.ftl"/>
<#include "lib/browse.ftl"/>

<#assign title = "Links"/>
<#if actionBean.tags?exists>
    <#assign title = title + " Tagged"/>
    <#list actionBean.tags.list as tag>
        <#assign title = title + " " + tag.name/>
    </#list>
</#if>

<@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
    title=title>

    <@s.layout_component name="body">
        <h2>${actionBean.links.total} links

        <#if actionBean.tags?exists>
        tagged
            <#list actionBean.tags.list as tag>
                <@s.link beanclass="dixie.web.action.LinksTaggedActionBean">
                    <@s.param name="format" value=actionBean.format.folder/>
                    <@s.param name="tags" value=tag.name/>
                    ${tag.name}
                </@s.link>
        <#if tag_has_next> <#nt></#if>
            </#list>
        </#if>
        </h2>

        <div>
        <@linkFormats/>
        </div>

        <@pageOrders enum="dixie.dao.order.LinkOrder"/>

        <ul>
            <#list actionBean.links.page as link>
                <li><@linkItem link=link/></li>
            </#list>
        </ul>

        <@pageLinks/>
        <@pageSizes/>

        <h4>Related tags</h4>

        <@relatedTags/>
    </@s.layout_component>

</@s.layout_render>