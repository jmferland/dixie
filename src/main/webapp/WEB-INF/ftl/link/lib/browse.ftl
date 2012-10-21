<#include "../../lib/core.ftl"/>

<#-- --------------------------------------------------------------------------
  -- linkFormats
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 12, 2009
  -- \brief Print a list of Link Format-s.
  -- --------------------------------------------------------------------------
  -->
<#macro linkFormats>
    <@s.format var="tags" value=actionBean.tags formatPattern="+"/>

    <#local params = {"format":"all"}/>
    <#if (tags?length > 0)>
        <#local params = params + {"tags":tags}/>
    </#if>

    <#if actionBean.format.folder != "all">
        <@beanLink label="All" params=params/>
 <#nt><#-- Add a space after this link. -->
    </#if>

    <#list actionBean.allFormats as oFormat>
        <#if actionBean.format.folder == oFormat.folder>
            <strong>${oFormat.folder?cap_first}</strong>
        <#else>
            <#local params = params + {"format":oFormat.folder}/>
            <@beanLink label=oFormat.folder?cap_first params=params/>
<#if oFormat_has_next> <#nt></#if><#-- Add a space after evey link (but the last). -->
        </#if>
    </#list>
</#macro>

<#-- --------------------------------------------------------------------------
  -- relatedTags
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Print a list of related tags.
  -- --------------------------------------------------------------------------
  -->
<#macro relatedTags beanclass="dixie.web.action.LinksTaggedActionBean"
    params={}>
<@s.format var="tmp" value=actionBean.tags formatPattern="+"/>
<#if (tmp?length > 0)>
    <#local tmp = tmp + "+"/>
</#if>

<ul>
    <#list actionBean.relatedTags.page as tag>
        <li><@compress single_line=true>
            <@s.link beanclass=beanclass>
                <#list params?keys as key>
                    <@s.param name=key value=params[key]/>
                </#list>
                <@s.param name="format" value=actionBean.format.folder/>
                <@s.param name="tags" value=tmp + tag.name/>
                ${tag.name}
            </@s.link>
            (${tag.count})
        </@compress></li>
    </#list>
</ul>
</#macro>

<#-- --------------------------------------------------------------------------
  -- linkItem
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created June 14, 2009
  -- \brief Print a formatted link list item.
  -- --------------------------------------------------------------------------
  -->
<#macro linkItem link>
<@compress single_line=true>
    <@s.url var="localUrl" beanclass="dixie.web.action.ViewLinkActionBean">
        <@s.param name="link" value=link.id/>
        <@s.param name="title" value=link.urlSafeTitle/>
    </@s.url>

    <h3><a href="${link.url}">${link.title}</a></h3>

    <#if link.smallThumb.isValid>
        <img src="${link.smallThumb.src}"/>
    </#if>

    <p><#if (link.host?length > 0)>${link.host} &#8212; </#if>${link.notes}</p>

    <div>
        <#list link.tags.list as tag>
            <@s.link beanclass="dixie.web.action.LinksTaggedActionBean">
                <@s.param name="tags" value=tag.name/>
                ${tag.name}
            </@s.link>
            <#if tag_has_next> </#if>
        </#list>
    </div>

    <div><a href="${localUrl}">${link.comments} comment<#if link.comments != 1>s</a></#if></div>

    <div>
        <@s.link beanclass="dixie.web.action.PromoteLinkActionBean">
            <@s.param name="link" value=link.id/>
            promote
        </@s.link>
    </div>

    <div>
        <@s.link beanclass="dixie.web.action.DemoteLinkActionBean">
            <@s.param name="link" value=link.id/>
            demote
        </@s.link>
    </div>

    <#-- \todo add support for favorite and share. -->
    <div>favorite (todo)</div>

    <div>share (todo)</div>

    <div>submitted by <@userStamp user=link.user gravatarSize=16/></div>
</@compress>
</#macro>