<#-- --------------------------------------------------------------------------
  -- loginUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 25, 2009
  -- \brief Return the login url with the referrer parameter set to the current
  -- servlet path by default.
  -- --------------------------------------------------------------------------
  -->
<#function loginUrl>
    <#-- Careful not to give the variable and function the same name. -->
    <@s.url var="tmp" beanclass="dixie.web.action.LoginActionBean">
        <@s.param name="referrer" value=requestURI/>
    </@s.url>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- registerUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 25, 2009
  -- \brief Return the register url with the referrer parameter set to the
  -- current servlet path by default.
  -- --------------------------------------------------------------------------
  -->
<#function registerUrl>
    <@s.url var="tmp" beanclass="dixie.web.action.RegisterActionBean">
        <@s.param name="referrer" value=requestURI/>
    </@s.url>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- logoutUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Return the logout url with the referrer parameter set to the
  -- current servlet path by default.
  -- --------------------------------------------------------------------------
  -->
<#function logoutUrl>
    <@s.url var="tmp" beanclass="dixie.web.action.LogoutActionBean">
        <@s.param name="referrer" value=requestURI/>
    </@s.url>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- profileUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 12, 2009
  -- \brief Return the profile url.
  -- --------------------------------------------------------------------------
  -->
<#function profileUrl user>
    <@s.url var="tmp" beanclass="dixie.web.action.user.ViewUserActionBean">
        <@s.param name="user" value=user.username/>
    </@s.url>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- homeUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Return the home url.
  -- --------------------------------------------------------------------------
  -->
<#function homeUrl>
    <@s.url var="tmp" beanclass="dixie.web.action.HomeActionBean"/>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- submitLinkUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Return the submit link url.
  -- --------------------------------------------------------------------------
  -->
<#function submitLinkUrl>
    <@s.url var="tmp" beanclass="dixie.web.action.AddLinkActionBean"/>
	<#return tmp/>
</#function>

<#-- --------------------------------------------------------------------------
  -- linksUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 11, 2009
  -- \brief Return the (view) links url.
  -- --------------------------------------------------------------------------
  -->
<#function linksUrl>
    <@s.url var="tmp" beanclass="dixie.web.action.LinksActionBean"/>
	<#return tmp/>
</#function>