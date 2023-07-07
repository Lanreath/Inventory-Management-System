SELECT
case
when INSTR(pr.productname, 'P71') > 0
then 'AMXSG_METAL'
when co.customername = 'CCL_APAC'
then 'CCLSG'
when co.customername = 'CVN'
then 'UOB VN'
when co.customername in ('TFWSG', 'TFW_APAC')
then 'TFW_AP'
else co.customername
END as customer,
get_token(pr.productname,1,'_') as product,
case
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF01' then '01_CLASSIC'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF02' then '02_PAINTER'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF03' then '03_ARCH'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC1' then '01_CLASSIC_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC2' then '02_PAINTER_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC3' then '03_ARCH_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,5) = 'VBW01' then 'PRADA_WEAR'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBM01' then 'PLAT'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUP1' then 'PLAT_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC1' then 'VBWC1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBW01' then 'VBW01'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC3' then 'VBWC3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWL1' then 'VBWL1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG2' then 'VBWG2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG3' then 'VBWG3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG4' then 'VBWG4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG5' then 'VBWG5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG6' then 'VBWG6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG7' then 'VBWG7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF1' then 'VBWF1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF4' then 'VBWF4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF5' then 'VBWF5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB1' then 'VBWB1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB2' then 'VBWB2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB3' then 'VBWB3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB4' then 'VBWB4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC4' then 'VBWC4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC2' then 'VBWC2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC6' then 'VBWC6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC7' then 'VBWC7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF3' then 'VBWF3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF6' then 'VBWF6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF7' then 'VBWF7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG1' then 'VBWG1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF2' then 'VBWF2'
when co.customername IN ('AMXSG', 'BOASG') then pr.productkey1
when co.customername IN ('SCBSG','SCBBR') then
  case
  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'SCB') > 0
  then get_token(pr.productalias,'1','_')
  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'PN') > 0
  then 
    case 
    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDX', 'PDX', 'EDX')
    then 'MDX/PDX/EDX'
    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDI', 'EDI')
    then 'MDI/EDI'
    else SUBSTR(co.externalcustomerorderid, 1, 3)
    end
  else get_token(co.externalcustomerorderid,1,'.')
  end
when co.customername = 'CCLSG' then CONCAT(CONCAT(get_token(pr.productalias,'1','_'), '-'), SUBSTR(pr.productkey1, 1, 4))
else get_token(pr.productalias,'1','_')
END as vaultname,
SUM(wo.quantity)
from customerorder co, workorder wo, product pr, part pt
where wo.productid = pr.productid
and pr.productkey1 = pt.productkey1
and pr.configurationid = pt.configurationid
and wo.workorderiddisplay = wo.workorderid
and wo.status <> 700
and wo.splitflag <> 1
and (get_token(pr.productname,'5','_') != 'RNW')
and co.customername <> 'CSG'
and  to_date(wo.creationdate,'DD/MM/YY') = to_date($P{PersoDate},'DD/MM/YY')
and wo.customerorderid = co.customerorderid
group by 
case
when INSTR(pr.productname, 'P71') > 0
then 'AMXSG_METAL'
when co.customername = 'CCL_APAC'
then 'CCLSG'
when co.customername = 'CVN'
then 'UOB VN'
when co.customername in ('TFWSG', 'TFW_APAC')
then 'TFW_AP'
else co.customername
END,
get_token(pr.productname,1,'_'),
case
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF01' then '01_CLASSIC'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF02' then '02_PAINTER'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBF03' then '03_ARCH'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC1' then '01_CLASSIC_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC2' then '02_PAINTER_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUC3' then '03_ARCH_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,5) = 'VBW01' then 'PRADA_WEAR'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBM01' then 'PLAT'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'MetVBUP1' then 'PLAT_UAE'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC1' then 'VBWC1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBW01' then 'VBW01'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC3' then 'VBWC3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWL1' then 'VBWL1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG2' then 'VBWG2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG3' then 'VBWG3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG4' then 'VBWG4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG5' then 'VBWG5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG6' then 'VBWG6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG7' then 'VBWG7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF1' then 'VBWF1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF4' then 'VBWF4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF5' then 'VBWF5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB1' then 'VBWB1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB2' then 'VBWB2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB3' then 'VBWB3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWB4' then 'VBWB4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC4' then 'VBWC4'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC2' then 'VBWC2'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC5' then 'VBWC5'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC6' then 'VBWC6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWC7' then 'VBWC7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF3' then 'VBWF3'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF6' then 'VBWF6'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF7' then 'VBWF7'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWG1' then 'VBWG1'
when SUBSTR(get_token(pr.productname,1,'_'),9,8) = 'VBWF2' then 'VBWF2'
when co.customername IN ('AMXSG', 'BOASG') then pr.productkey1
when co.customername IN ('SCBSG','SCBBR') then
  case
  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'SCB') > 0
  then get_token(pr.productalias,'1','_')
  when INSTR(get_token(co.externalcustomerorderid,1,'.'), 'PN') > 0
  then 
    case 
    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDX', 'PDX', 'EDX')
    then 'MDX/PDX/EDX'
    when SUBSTR(co.externalcustomerorderid, 1, 3) IN ('MDI', 'EDI')
    then 'MDI/EDI'
    else SUBSTR(co.externalcustomerorderid, 1, 3)
    end
  else get_token(co.externalcustomerorderid,1,'.')
  end
when co.customername = 'CCLSG' then CONCAT(CONCAT(get_token(pr.productalias,'1','_'), '-'), SUBSTR(pr.productkey1, 1, 4))
else get_token(pr.productalias,'1','_')
END
order by customer, product, vaultname
-----------------------
select DISTINCT
co.inputfilename,
to_char(sysdate,'DD/MM/YYYY HH24:MI') as printdate,
to_char(ca.workorderid, '0XXXXXXX') as wid,
TRIM(TO_CHAR(WO.WORKORDERID,'XXXXXX'))AS HEX_WO,
wo.persofileref,
to_char(sysdate,'DD/MM/YYYY') as ReportDate,
wo.quantity,
get_token(pr.productname,'1','_')|| '-' as pname1,
substr(pr.productkey1,1,4) as pname2,
case
	when get_token(ca.exportedkeyvalue4,'9',';') ='00010' then 'Net App'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00016' then 'Net App'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00022' then 'Navitas'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00048' then 'Mc Premium'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00049' then 'CNPC Silver'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00050' then 'CNPC Gold'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00052' then 'Honeywell'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00067' then 'Bayer'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00068' then 'AIA'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00069' then 'AIA'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00071' then 'APMM TERMINAL'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00072' then 'APM DAMCO'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00073' then 'APM MAERSK LINE'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00076' then 'APMM TERMINAL'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00077' then 'APM DAMCO'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00078' then 'APM MAERSK LINE'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00079' then 'APM MAERSK TANKER'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00080' then 'APM SVITZER'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00081' then 'APM MAERSK DRILLING'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00082' then 'APM DAMCO'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00083' then 'APM MAERSK LINE'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00084' then 'APM SVITZER'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00085' then 'APM MAERSK DRILLING'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00089' then 'NBC UNIVERSAL'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00104' then 'GENERAL MOTORS'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00106' then 'SAMSUNG TAIWAN'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00107' then 'FONTERRA'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00120' then 'SCHRODERS'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00123' then 'JTI'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00149' then 'WHITE CARD PLASTIC'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00157' then 'ICARE Pcard'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00164' then 'NETFLIX'
	when get_token(ca.exportedkeyvalue4,'9',';') ='00167' then 'PALANTIR'
else
	substr(pr.productkey1,1,4)
end as pname3,
pr.productkey1,
pr.productkey4,
pr.productname,
get_token(pr.productalias,'2','_') as location,
pt.partname,
(select
case
	when get_token(ca.exportedkeyvalue4,'9',';') ='00010' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00016' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00022' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00048' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00049' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00050' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00052' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00067' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00068' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00069' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00071' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00072' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00073' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00076' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00077' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00078' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00079' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00080' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00081' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00082' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00083' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00084' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00085' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00089' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00104' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00106' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00107' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00120' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00123' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00149' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00157' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00164' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
	when get_token(ca.exportedkeyvalue4,'9',';') ='00167' then count(get_token(ca2.exportedkeyvalue4,'9',';'))
else
	wo.quantity
end from 
(select workorderid, exportedkeyvalue4 from card  union all
 select workorderid, exportedkeyvalue4 from card_arc) ca2 
where ca2.workorderid=ca.workorderid
and get_token(ca2.exportedkeyvalue4,'9',';')=get_token(ca.exportedkeyvalue4,'9',';')) as mCnt


from customerorder co,workorder wo,product pr, part pt,
(select workorderid, exportedkeyvalue4 from card  union all
 select workorderid, exportedkeyvalue4 from card_arc) ca
where co.customerorderid= wo.customerorderid
and wo.productid = pr.productid
and ca.workorderid=wo.workorderid
and pr.productkey1 = pt.productkey1
and pr.configurationid = pt.configurationid
--and wo.splitflag <> 1
--and wo.mergeflag <> 1
and wo.workorderiddisplay = wo.workorderid
and wo.customerorderid  = co.customerorderid
and co.customername in ('CCLSG','CCL_APAC')
and wo.status <> 700
and substr(get_token(pr.productname,'1','_'),1,2) <> 'CH'
and (get_token(pr.productname,'5','_') != 'RNW')
and  to_date(wo.creationdate,'DD/MM/YY') = to_date($P{PersoDate},'DD/MM/YY')
order by ReportDate,co.inputfilename, pr.productname,HEX_WO
--version control
--version 1.1 : remove inputfilename from order by to improve groupability