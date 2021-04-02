abstract class FilenameSuffix {
    abstract fun nextSuffix(): String
}

class StringSuffix: FilenameSuffix() {
    private val min = 'a'
    private val max = 'z'
    private val suffixLen = 2
    private var suffix: Array<Char> = Array(suffixLen) { min }

    override fun nextSuffix(): String {
        val result = suffix.joinToString(separator = "")
        for (i in suffix.indices.reversed()) {
            if (suffix[i] < max) {
                suffix[i]++

                for (resetIdx in i + 1 until suffixLen)
                    suffix[resetIdx] = min
                break
            }
        }
        return result
    }
}

class DecimalSuffix: FilenameSuffix() {
    private val initial = 1
    private var suffix = initial
    override fun nextSuffix(): String {
        val result = suffix.toString()
        suffix++
        return result
    }
}