SELECT 
customer, product, vaultname, SUM(qty)
FROM (
select
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
when co.customername = 'AMEX_BAHRAIN' then
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
    else SUBSTR(get_token(pr.productname,1,'_'),9,8)
    end
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
when co.customername = 'CCLSG' then
get_token(pr.productalias,'1','_') || '-' || 
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
    else SUBSTR(pr.productkey1,1,4)
    end
else get_token(pr.productalias,'1','_')
END as vaultname,
wo.quantity as qty,
from customerorder co, workorder wo, product pr, part pt, card ca
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
and ca.workorderid=wo.workorderid
) as summary
GROUP BY customer, product, vaultname
ORDER BY vaultname