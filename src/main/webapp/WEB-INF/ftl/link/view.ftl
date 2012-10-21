<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>
<#include "../comment/lib/comment.ftl"/>
<#include "../user/lib/user.ftl"/>

<@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
    title=actionBean.link.title>

    <@s.layout_component name="messages"/>

    <@s.layout_component name="body">
        <h2><a href="${actionBean.link.url?html}">${actionBean.link.title}</a></h2>

        <p><strong>// TODO: add other link details</strong></p>

        <@s.messages/>

        <#if actionBean.link.smallThumb.isValid>
        <p><img src="${actionBean.link.smallThumb.src}"/></p>
        </#if>

        <p>${actionBean.link.notes}</p>

        <#if actionBean.link.user?exists>
        <div class="submittedBy">
            <h2>Submitted by</h2>
            <@userStamp user=actionBean.link.user gravatarSize=80>
                <div>${actionBean.link.createdOn?datetime}</div>
            </@userStamp>
        </div>
        </#if>

        <h3>${actionBean.comments.total} comment<#if (actionBean.comments.total != 1)>s</#if>, ${actionBean.link.comments - actionBean.comments.total} repl<#if (actionBean.link.comments - actionBean.comments.total != 1)>ies<#else>y</#if></h3>

        <@pageOrderForm enum="dixie.dao.order.CommentOrder"/>

        <@commentTree comments=actionBean.comments.page/>
        <@pageLinks/>
        <@pageSizes/>

        <h3>Add a comment</h3>
        <#if (actionBean.loggedInUser?exists)>
        <@s.form beanclass=actionBean.beanclass>
            <@s.errors/>
            <@FormRow>
                <@s.label for="comment.replyTo"/>
                <@s.select name="replyTo" id="comment.replyTo">
                    <@s.option value=0>[ The link ]</@s.option>
                    <@selectComment comments=actionBean.comments.page/>
                </@s.select>
            </@FormRow>
            <@FormRow>
                <@s.label for="comment.text"/>
                <@s.textarea name="comment.text" id="comment.text"/>
            </@FormRow>
            <@include beanclass="dixie.web.action.AskCaptchaActionBean"/>
            <@s.param name="link" value=actionBean.link.id/>
            <@s.param name="title" value=actionBean.link.urlSafeTitle/>
            <@s.param name="page.index" value=actionBean.page.index/>
            <@s.submit name="addComment" value="Submit"/>
        </@s.form>

        <#else>
        <p>You must <a href="${loginUrl()}">log in</a> as a <a href="${registerUrl()}">registered</a> user to add a comment.</p>
        </#if>
    </@s.layout_component>

</@s.layout_render>

<#-- --------------------------------------------------------------------------
  -- commentTree
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 25, 2009
  -- \brief Recursively write out a tree of comments and replies.
  -- --------------------------------------------------------------------------
  -->
<#macro commentTree comments=[]>
<@compress single_line=true>
<ul>
    <#list comments as comment>
        <li id="comment-${comment.id}">
            <@commentItem comment=comment/>
            <@commentTree comments=comment.replies/>
        </li>
    </#list>
</ul>
</@compress>
</#macro>

<#-- --------------------------------------------------------------------------
  -- selectComment
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 26, 2009
  -- \brief Recursively display comment options.
  -- --------------------------------------------------------------------------
  -->
<#macro selectComment comments=[] idPath="" depth=0>
<#compress>
<#list comments as comment>
    <@s.option value=comment.id><@repeat depth>&nbsp;</@repeat>#${idPath}${comment_index + 1}: ${comment.user.username}, <#if (comment.text?length > 30)>${comment.text?substring(0,30)}...<#else>${comment.text}</#if></@s.option>
    <@selectComment comments=comment.replies idPath=(idPath + (comment_index + 1) + ".") depth=(depth + 1)/>
</#list>
</#compress>
</#macro>