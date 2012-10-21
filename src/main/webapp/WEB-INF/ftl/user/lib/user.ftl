<#-- --------------------------------------------------------------------------
  -- gravatarUrl
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 30, 2009
  -- \brief Return a gravatar url.
  -- --------------------------------------------------------------------------
  -->
<#function gravatarUrl user size=80>
    <#return "http://gravatar.com/avatar/" + user.md5email + "?s=" + size + "&d=identicon"/>
</#function>

<#-- --------------------------------------------------------------------------
  -- userStamp
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created May 31, 2009
  -- \brief Return a stamp/ signature for the given user.
  -- --------------------------------------------------------------------------
  -->
<#macro userStamp user gravatarSize=16>
<@compress single_line=true>
    <span class="user">
        <@s.link beanclass="dixie.web.action.user.ViewUserActionBean">
            <@s.param name="user" value=user.username/>
            <img src="${gravatarUrl(user, gravatarSize)}"/>${user.username}
        </@s.link>
        <#nested>
    </span>
</@compress>
</#macro>