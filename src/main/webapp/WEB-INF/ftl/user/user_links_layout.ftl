<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>
<#include "../link/lib/browse.ftl"/>
<#include "lib/user.ftl"/>

<#assign tagList = ""/>
<#if actionBean.tags?exists>
    <#assign tagList = " Tagged"/>
    <#list actionBean.tags.list as tag>
        <#assign tagList = tagList + " " + tag.name/>
    </#list>
</#if>

<@s.layout_definition>
    <@s.layout_render name="/WEB-INF/ftl/user/user_layout.ftl"
        title=actionBean.user.username + " / " + title + tagList
        user=actionBean.user>

        <@s.layout_component name="body">
            <h3>${actionBean.links.total} ${header}

            <#if actionBean.tags?exists>
            tagged
                <#list actionBean.tags.list as tag>
                    <@s.link beanclass=taggedBeanclass>
                        <@s.param name="user" value=actionBean.user.username/>
                        <@s.param name="format" value=actionBean.format.folder/>
                        <@s.param name="tags" value=tag.name/>
                        ${tag.name}
                    </@s.link>
            <#if tag_has_next> <#nt></#if>
                </#list>
            </#if>
            </h3>

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

            <@relatedTags beanclass=taggedBeanclass
                params={"user":actionBean.user.username}/>
        </@s.layout_component>

    </@s.layout_render>
</@s.layout_definition>