package org.elixir_lang.heex.xml

/**
 * Splits a HEEx tag name into the component it refers to, mirroring
 * `Phoenix.LiveView.HTMLEngine.classify_type/1` and `decompose_remote_component_tag!/3`: a name
 * starting with `.` is a local component; a name starting with an uppercase letter whose last
 * `.`-separated segment starts lowercase is a remote component (the segments before the last dot
 * are the module alias chain, exactly as written in the source). Any other name - including a
 * bare uppercase name with no dot, which real Phoenix itself fails to compile - is not a
 * component and this returns `null`.
 */
sealed class ComponentTagName {
    data class Local(val functionName: String) : ComponentTagName()
    data class Remote(val aliasChain: String, val functionName: String) : ComponentTagName()

    companion object {
        @JvmStatic
        fun parse(tagName: String): ComponentTagName? = when {
            tagName.startsWith(".") ->
                tagName.removePrefix(".").takeIf { it.isNotEmpty() }?.let(::Local)
            tagName.isNotEmpty() && tagName[0].isUpperCase() ->
                parseRemote(tagName)
            else ->
                null
        }

        private fun parseRemote(tagName: String): Remote? {
            val lastDot = tagName.lastIndexOf('.')
            if (lastDot == -1) {
                return null
            }

            val functionName = tagName.substring(lastDot + 1)
            val aliasChain = tagName.substring(0, lastDot)

            return if (functionName.isNotEmpty() && functionName[0].isLowerCase()) {
                Remote(aliasChain, functionName)
            } else {
                null
            }
        }
    }
}
