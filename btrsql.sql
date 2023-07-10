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
    when ca.custom ='00010' then 'Net App'
    when ca.custom ='00016' then 'Net App'
    when ca.custom ='00022' then 'Navitas'
    when ca.custom ='00048' then 'Mc Premium'
    when ca.custom ='00049' then 'CNPC Silver'
    when ca.custom ='00050' then 'CNPC Gold'
    when ca.custom ='00052' then 'Honeywell'
    when ca.custom ='00067' then 'Bayer'
    when ca.custom ='00068' then 'AIA'
    when ca.custom ='00069' then 'AIA'
    when ca.custom ='00071' then 'APMM TERMINAL'
    when ca.custom ='00072' then 'APM DAMCO'
    when ca.custom ='00073' then 'APM MAERSK LINE'
    when ca.custom ='00076' then 'APMM TERMINAL'
    when ca.custom ='00077' then 'APM DAMCO'
    when ca.custom ='00078' then 'APM MAERSK LINE'
    when ca.custom ='00079' then 'APM MAERSK TANKER'
    when ca.custom ='00080' then 'APM SVITZER'
    when ca.custom ='00081' then 'APM MAERSK DRILLING'
    when ca.custom ='00082' then 'APM DAMCO'
    when ca.custom ='00083' then 'APM MAERSK LINE'
    when ca.custom ='00084' then 'APM SVITZER'
    when ca.custom ='00085' then 'APM MAERSK DRILLING'
    when ca.custom ='00089' then 'NBC UNIVERSAL'
    when ca.custom ='00104' then 'GENERAL MOTORS'
    when ca.custom ='00106' then 'SAMSUNG TAIWAN'
    when ca.custom ='00107' then 'FONTERRA'
    when ca.custom ='00120' then 'SCHRODERS'
    when ca.custom ='00123' then 'JTI'
    when ca.custom ='00149' then 'WHITE CARD PLASTIC'
    when ca.custom ='00157' then 'ICARE Pcard'
    when ca.custom ='00164' then 'NETFLIX'
    when ca.custom ='00167' then 'PALANTIR'
    else SUBSTR(pr.productkey1,1,4)
    end
else get_token(pr.productalias,'1','_')
end as vaultname,
wo.quantity as qty
from customerorder co, workorder wo, product pr, part pt, (
SELECT DISTINCT workorderid, get_token(exportedkeyvalue4,'9',';') as custom FROM card
) ca
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
and ca.workorderid = wo.workorderid
) summary
GROUP BY customer, product, vaultname
ORDER BY customer, vaultname;