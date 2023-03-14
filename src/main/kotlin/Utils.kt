package org.mirai.qqBotMirai
class Utils {
    companion object {
        fun spellSearchFormatter(spellList: List<Spell>): String {
            var spells = "泉津的法术大全检索：\n\n"
            spellList.take(5).forEach(fun(spell: Spell) {
                var spellLevel = ""
                spell.classes.forEach(fun(c) {
                    spellLevel += c.key + " " + c.value + ", "
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

                val range = if (spell.range.value == null)
                    spell.range.units
                else
                    spell.range.value + ' ' + spell.range.units

                var subschool = ""
                if (spell.subschool != "")
                    subschool += '[' + spell.subschool + ']'

                var domain = ""
                spell.domain.forEach(fun(d) { domain += d.key + " " + d.value + ", " })
                spell.subDomain.forEach(fun(d) { domain += d.key + " " + d.value + ", " })
                if (domain.isNotBlank())
                    domain = domain.substring(0, domain.length - 2)

                spells += spell.name + '\n' +
                    "学派 " + spell.school + (if (subschool.isNotBlank()) ("($subschool)") else "") +
                    (if (spell.types.isNotBlank()) ('[' + spell.types + ']') else "") + '\n' +
                    (if (domain.isNotBlank()) ("领域 $domain\n") else "") +
                    "等级 " + spellLevel + '\n' +
                    "动作 " + spell.action.cost + ' ' + spell.action.type + '\n' +
                    "成分 " + components + '\n' +
                    "距离 " + range + '\n' +
                    (if (spell.area.isNotBlank()) ("范围 " + spell.area + '\n') else "") +
                    (if (spell.effect.isNotBlank()) ("效果 " + spell.effect + '\n') else "") +
                    (if (spell.target.isNotBlank()) ("目标 " + spell.target + '\n') else "") +
                    "持续时间 " + spell.duration.value + '\n' +
                    (if (spell.save.isNotBlank()) ("豁免 " + spell.save + '\n') else "") +
                    "法术抗力 " + (if (spell.sr.isNotBlank()) "可" else "否") + '\n' +
                    spell.shortDescription + "\n\n"
            })
            return spells
        }
    }
}