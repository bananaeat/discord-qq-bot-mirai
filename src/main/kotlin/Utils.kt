package org.example.mirai.plugin
class Utils {
    companion object {
        fun spellSearchFormatter(spellList: List<Spell>): String {
            var spells = "泉津的法术大全检索：\n\n"
            spellList.subList(0, 5).forEach(fun(spell: Spell) {
                var spellLevel = ""
                spell.classes.forEach(fun(c: List<ItemValue>) {
                    spellLevel += c[0].toString() + " " + c[1].toString() + ", "
                })
                spellLevel = spellLevel.substring(0, spellLevel.length - 2)

                var components = ""
                spell.components.keys.forEach(fun(c: String) {
                    components += if (c != "materials")
                        "$c, "
                    else
                        c + '(' + spell.materials["value"] +
                            (if ("gpValue" in spell.materials.keys) (", " + spell.materials["gpValue"] + "gp") else "")
                })
                components = components.substring(0, components.length - 2)

                val range = if (spell.range.value == "")
                    spell.range.units
                else
                    spell.range.value + ' ' + spell.range.units

                var subschool = ""
                if (spell.subSchool != "")
                    subschool += '[' + spell.subSchool + ']'

                var domain = ""
                spell.domain.forEach(fun(d) { domain += d[0].toString() + " " + d[1].toString() + ", " })
                spell.subDomain.forEach(fun(d) { domain += d[0].toString() + " " + d[1].toString() + ", " })
                if (domain.isNotBlank())
                    domain = domain.substring(0, domain.length - 2)

                spells += spell.name + '\n' +
                    "学派 " + spell.school + (if (spell.subSchool.isNotBlank()) ('(' + spell.subSchool + ')') else "") +
                    (if (spell.types.isNotBlank()) ('[' + spell.types + ']') else "") + '\n' +
                    (if (domain.isNotBlank()) ("领域 $domain\n") else "") +
                    "等级 " + spellLevel + '\n' +
                    "动作 " + spell.action.cost + ' ' + spell.action.type + '\n' +
                    "成分 " + components + '\n' +
                    "距离 " + range + '\n' +
                    (if (spell.area.isNotBlank()) ("范围 " + spell.area + '\n') else "") +
                    (if (spell.effect.isNotBlank()) ("效果 " + spell.effect + '\n') else "") +
                    (if (spell.target.isNotBlank()) ("目标 " + spell.target + '\n') else "") +
                    "持续时间 " + spell.duration + '\n' +
                    (if (spell.save.isNotBlank()) ("豁免 " + spell.save + '\n') else "") +
                    "法术抗力 " + (if (spell.sr.isNotBlank()) "可" else "否") + '\n' +
                    spell.shortDescription + "\n\n"
            })
            return spells
        }
    }
}