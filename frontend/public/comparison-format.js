/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
import {buildPayload,csvEncode} from './operations-utils.js';
export const kindLabels={ADDED:'新增',REMOVED:'移除',CHANGED:'有变化',UNCHANGED:'无变化'};
export const transitionLabels={NEW:'新增',REMOVED:'移除',RELAXED:'建议较宽松',TIGHTENED:'建议更严格',SAME:'建议等级相同'};
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function stringRows(rows){return rows.map(row=>Object.fromEntries(Object.entries(row).map(([k,v])=>[k,String(v)])));}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function scenario(meta,rows,asOf){return buildPayload(meta.fields,rows,asOf);}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function makeSnapshot(meta,input){if(!meta||!/^[0-9a-f]{64}$/.test(meta.ruleFingerprint)||!/^[0-9a-f]{64}$/.test(meta.contractFingerprint)||!meta.project)throw new Error('请先加载当前字段契约');return {format:'zhuatech-review-input/v1',project:meta.project,contractFingerprint:meta.contractFingerprint,ruleFingerprint:meta.ruleFingerprint,scenario:scenario(meta,input.records,input.asOf)};}
/** 导入响应验证后构造新状态，不修改现有输入。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function importedScenario(meta,checked){if(!checked||!checked.scenario||!Array.isArray(checked.scenario.records))throw new Error('导入校验响应格式错误');const rows=stringRows(checked.scenario.records);return scenario(meta,rows,checked.scenario.asOf);}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function filteredRows(report,kind='',search=''){const query=search.trim().toLowerCase();return report.rows.filter(r=>(!kind||r.kind===kind)&&(!query||String(r.id).toLowerCase().includes(query)));}
/** 全量导出，不受当前筛选和分页影响；精确负数仍需防CSV公式。上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function comparisonCsv(report,meta){const labels=Object.fromEntries([...(meta.columns||[]),{key:'decision',label:'核算建议'},{key:'reason',label:'核对依据'},{key:'id',label:'记录编号'}].map(c=>[c.key,c.label]));const rows=[['编号','变化类型','建议变化','字段','原值','新值','精确数值差（新减原）']];for(const r of report.rows){const changes=r.changes.length?r.changes:[{field:'—',before:'—',after:'—',numericDelta:null}];for(const c of changes)rows.push([r.id,kindLabels[r.kind],transitionLabels[r.transition],labels[c.field]||c.field,c.before,c.after,c.numericDelta]);}return csvEncode(rows);}
