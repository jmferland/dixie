<#include "../../user/lib/user.ftl"/>

<#-- --------------------------------------------------------------------------
  -- commentItem
  -- --------------------------------------------------------------------------
  -- \author Jonathan Ferland
  -- \created July 19, 2009
  -- \brief Print a formatted comment list item.
  -- --------------------------------------------------------------------------
  -->
<#macro commentItem comment>
<@compress single_line=true>
    <@userStamp user=comment.user> ${comment.createdOn?datetime}</@userStamp>
    <div>${comment.text?html}</div>
</@compress>
</#macro>