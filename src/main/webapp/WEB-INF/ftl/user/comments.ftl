<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>
<#include "../comment/lib/comment.ftl"/>
<@s.layout_render name="/WEB-INF/ftl/user/user_layout.ftl"
    title=actionBean.user.username + " / Comments"
    user=actionBean.user>

    <@s.layout_component name="body">
        <h3>${actionBean.comments.total} comment<#if actionBean.comments.total != 1>s</#if></h3>

        <@pageOrders enum="dixie.dao.order.CommentOrder"/>

        <ul>
            <#list actionBean.comments.page as comment>
                <#-- TODO: should be different, don't want to allow voting up/ down from here.
                     Also, might want to indicate link this is for. -->
                <li><@commentItem comment=comment/></li>
            </#list>
        </ul>

        <@pageLinks/>
        <@pageSizes/>
    </@s.layout_component>

</@s.layout_render>