<#assign s=JspTaglibs["/WEB-INF/stripes-freemarker.tld"]>
<#setting url_escaping_charset="UTF-8">
<#include "web/urls.ftl"/>

<#-- --------------------------------------------------------------------------
  -- include
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 24, 2009
  -- \brief Include the contents of another web application resource into the
  -- output. This is necessary as a workaround for the prependContext attribute
  -- for the url/ link tags breaking when set to false (but not true? strange).
  -- --------------------------------------------------------------------------
  -->
<#macro include beanclass params={}>
    <@s.url var="tmp" beanclass=beanclass/>
    <#if tmp?starts_with(contextPath)>
        <#local tmp = tmp?substring(contextPath?length)/>
    </#if>
    <@include_page path=tmp params=params/>
</#macro>

<#-- --------------------------------------------------------------------------
  -- repeat
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 26, 2009
  -- \brief Repeat the nested the given number of times.
  -- --------------------------------------------------------------------------
  -->
<#macro repeat count>
<#compress>
    <#if (count >= 1)>
        <#list 1..count as x><#nested></#list>
    </#if>
</#compress>
</#macro>

<#-- --------------------------------------------------------------------------
  -- pageOrders
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Print a list of page order options.
  -- --------------------------------------------------------------------------
  -->
<#macro pageOrders enum>
    <p class="pageOrders">
        <#list enums[enum]?keys as order>
            <#if actionBean.page.order.intValue == enums[enum][order].intValue>
                <strong>${enums[enum][order]}</strong>
            <#else>
                <@beanLink params={"page.index":1, "page.order":enums[enum][order].intValue} label=enums[enum][order]/>
            </#if>
 <#nt><#-- Add a space after evey link. -->
        </#list>
    </p>
</#macro>

<#-- --------------------------------------------------------------------------
  -- pageOrderForm
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 12, 2009
  -- \brief Print a form to select from page order options.
  -- --------------------------------------------------------------------------
  -->
<#macro pageOrderForm enum>
    <@beanForm params={"page.index":1}>
        <@s.select name="page.order">
            <@s.options_enumeration enum=enum/>
        </@s.select>
        <@s.submit name="redirect" value="Go"/>
    </@beanForm>
</#macro>

<#-- --------------------------------------------------------------------------
  -- pageLinks
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 30, 2009
  -- \brief Print a list of links to pages.
  -- --------------------------------------------------------------------------
  -->
<#macro pageLinks>
<@compress single_line=true>
    <#local pageParamKey="page.index"/>
    <#local currentPage=actionBean.page.index/>
    <#local maxPage=actionBean.page.max/>

    <p class="pageLinks"><#t>

    <#local prev = "&lt;&lt; Previous"/>
    <#if (currentPage != 1)>
        <@beanLink params={pageParamKey:(currentPage - 1)} label=prev/>
    <#else>
        ${prev} <#lt>
    </#if>

    <#local pages = pageList(currentPage, maxPage, 2, 3)/>
    <#local lastPage = 0/><#-- Assumes we always start at 1. -->
    <#list pages as page>
        <#if (page_has_next && lastPage != page - 1)>
            ... <#lt>
        </#if>

        <#if (currentPage == page)>
            ${currentPage}<#t>
        <#else>
            <@beanLink params={pageParamKey:page} label=page/>
        </#if>
 <#nt><#-- Add a space after evey page. -->
        <#local lastPage = page/>
    </#list>

    <#local next = "Next &gt;&gt;"/>
    <#if (currentPage < maxPage)>
        <@beanLink params={pageParamKey:(currentPage + 1)} label=next/>
    <#else>
        ${next}<#t>
    </#if>

    </p>
</@compress>
</#macro>

<#-- --------------------------------------------------------------------------
  -- pageSizes
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Print a list of page size options.
  -- --------------------------------------------------------------------------
  -->
<#macro pageSizes sizes=[15, 30, 50]>
<@compress single_line=true>
    <p class="pageSizes">
        <#list sizes as size>
            <#if actionBean.page.size == size>
                <strong>${size}</strong>
            <#else>
                <@beanLink params={"page.index":1, "page.size":size} label=size/>
            </#if>
 <#nt><#-- Add a space after evey link. -->
        </#list>
        per page
    </p>
</@compress>
</#macro>

<#-- --------------------------------------------------------------------------
  -- beanLink
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 30, 2009
  -- \brief Return a link to the current (action?) bean using the given label
  -- and overriding existing params with those given.
  -- --------------------------------------------------------------------------
  -->
<#macro beanLink label params={} forgetPageInfo=true>
    <@s.link beanclass=actionBean.beanclass>
        <#list params?keys as key>
            <@s.param name=key value=params[key]/>
        </#list>

        <#if forgetPageInfo>
            <#local params = params + {"page.index":0, "page.order":0, "page.size":0}/>
        </#if>

        <#list actionBean.params?keys as key>
            <#-- Only add keys not added by the overriding params map. -->
            <#if !params[key]?exists>
                <@s.param name=key value=actionBean.params[key]/>
            </#if>
        </#list>
        ${label}
    </@s.link>
</#macro>

<#-- --------------------------------------------------------------------------
  -- beanForm
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 12, 2009
  -- \brief Return a form to the current (action?) bean with the nested content
  -- put inside the form tag and overriding existing params with those given.
  -- --------------------------------------------------------------------------
  -->
<#macro beanForm params={} forgetPageInfo=true>
    <@s.form beanclass=actionBean.beanclass>
        <#nested>

        <#list params?keys as key>
            <@s.param name=key value=params[key]/>
        </#list>

        <#if forgetPageInfo>
            <#local params = params + {"page.index":0, "page.order":0, "page.size":0}/>
        </#if>

        <#list actionBean.params?keys as key>
            <#-- Only add keys not added by the overriding params map. -->
            <#if !params[key]?exists>
                <@s.param name=key value=actionBean.params[key]/>
            </#if>
        </#list>
    </@s.form>
</#macro>