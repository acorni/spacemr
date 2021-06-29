
usage:
     cd .../application; gradle run -Pargs="toolDb templateTable DbTools_test app_user" 


<#assign tname   = table.getName()>
<#assign tnameid = tname+"_id">
<#assign tnameU  = tools.stringUpcaseFirst(tname) >
<#assign mvcPrefixU = tools.stringUpcaseFirst(mvcPrefix) >

Table content:
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  ${cname} ${ctype}
</#foreach>

