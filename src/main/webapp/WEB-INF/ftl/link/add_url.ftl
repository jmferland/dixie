<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>

<@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
    title="Submit">

    <@s.layout_component name="body">
        <@s.form beanclass="dixie.web.action.AddLinkActionBean">
            <@s.errors/>
            <@FormRow>
                <@s.label for="link.url"/>
                <@s.text name="link.url" id="link.url"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="link.format"/>
                <@SelectFormat a_lstFormats=actionBean.allFormats/>
            </@FormRow>
            <@s.submit name="details" value="Next"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>

<#-- --------------------------------------------------------------------------
  -- SelectFormat
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 10, 2009
  -- \brief Display a list of format options.
  -- --------------------------------------------------------------------------
  -->
<#macro SelectFormat a_lstFormats=[]>
<@compress single_line=true>
<#list a_lstFormats as oFormat>
    <#local sOptionId = "l_f_" + oFormat.id />
    <@s.radio name="link.format" value=oFormat.id id=sOptionId/><@s.label for=sOptionId>${oFormat.name}</@s.label>
</#list>
</@compress>
</#macro>