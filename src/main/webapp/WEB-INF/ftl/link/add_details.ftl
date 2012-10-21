<#include "../lib/core.ftl"/>
<#include "../lib/web/form.ftl"/>

<@s.layout_render name="/WEB-INF/ftl/main_layout.ftl"
    title="Submit Details">

    <@s.layout_component name="body">
        <@s.form beanclass="dixie.web.action.AddLinkActionBean">
            <@s.errors/>
            <@FormRow>
                <@s.label for="link.title"/>
                <@s.text name="link.title" id="link.title"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="link.notes"/>
                <@s.textarea name="link.notes" id="link.notes"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="link.tags"/>
                <@s.text name="link.tags" id="link.tags"/>
            </@FormRow>
            <@FormRow>
                <@s.label for="link.sourceThumbUrl"/>
                <@s.text name="link.sourceThumbUrl" id="link.sourceThumbUrl"/>
            </@FormRow>
            <@include beanclass="dixie.web.action.AskCaptchaActionBean"/>
            <@s.submit name="save" value="Submit"/>
            <@s.submit name="url" value="Back"/>
        </@s.form>
    </@s.layout_component>

</@s.layout_render>