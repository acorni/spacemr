<#--
 questo e' un commmento
-->
<#assign n   = spacemr_notifica>
<#assign p   = spacemr_space_people_book>
<p>
Gentile ${p.getString("spacemr_responsible_first_name")} ${p.getString("spacemr_responsible_last_name")}
</p><p>

Lo stato della 'presenza' di ${p.getString("spacemr_people_first_name")} ${p.getString("spacemr_people_last_name")} dal ${date_from} al ${date_to}
con ripetizione
<#if p.getString("repetition") == "d" >
 giornaliera
<#else>
 settimanale
</#if>

</p><p>
e' ora in stato [${p.getString("stato")}]

</p><p>
Eventuali note:
</p><pre>
${p.getString("nota")}
</pre><p>

<a href='https://web.ing.unimo.it/spacemr/#?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update&spacemr_space_people_book_id=${p.getInt("spacemr_space_people_book_id")}'>
Vai a 'questa' Presenza</a><br/>

<a href='https://web.ing.unimo.it/spacemr/#?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list&qparams={%22allcolumns%22:[%22reason%22,%22date_from%22,%22date_to%22,%22repetition%22,%22stato%22,%22nota%22,%22spacemr_people_username%22,%22spacemr_people_first_name%22,%22spacemr_people_last_name%22,%22spacemr_people_role%22,%22spacemr_responsible_username%22,%22spacemr_responsible_first_name%22,%22spacemr_responsible_last_name%22,%22spacemr_responsible_role%22,%22spacemr_space_code%22,%22spacemr_space_description%22,%22transactions%22],%22pageNumber%22:1,%22pages%22:1,%22labelPrefix%22:%22db.spacemr_space_people_book%22,%22columns%22:[%22transactions%22,%22spacemr_space_code%22,%22spacemr_responsible_last_name%22,%22spacemr_people_last_name%22,%22reason%22,%22date_from%22,%22date_to%22,%22repetition%22],%22tableid%22:%22table_spacemr_space_people_book_list%22,%22pageSize%22:50,%22where%22:{%22this_username%22:true,%22from_today%22:true}}'>Riepilogo delle proprie 'prerenze'</a><br/>

<a href='https://web.ing.unimo.it/spacemr/#?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar&spacemr_space_id=1'>Calendario presenze del DIEF</a>

</p><p>
Cordiali Saluti<br/>
Il sistema di prenotazione spazi del Dief


<#--
--- notifica
 ${n.toString(2)}
--- prenotazine
${p.toString(2)}
fffffffff
-->
