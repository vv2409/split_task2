import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SplitTest {

    private val EOL = System.lineSeparator()

    private fun assertFileContent(fileName: String, expected: String) {
        val actualFile = File(fileName)

        assertTrue(actualFile.exists(), "File $actualFile does not exist!")

        assertEquals(expected, actualFile.readText())
    }

    private fun createFile(fileName: String, content: String) {
        File(fileName).writeText(content)
    }

    private fun removeFile(fileName: String) {
        File(fileName).delete()
    }

    @Test
    fun testNames() {
        createFile("input", "hello world")

        with(Split()) {
            parse(("input").split(" ").toTypedArray())
            split()
        }
        assertFileContent("xaa", "hello world$EOL")
        removeFile("xaa")

        with(Split()) {
            parse(("input -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("x1", "hello world$EOL")
        removeFile("x1")

        with(Split()) {
            parse(("input -o output").split(" ").toTypedArray())
            split()
        }
        assertFileContent("outputaa", "hello world$EOL")
        removeFile("outputaa")

        with(Split()) {
            parse(("input -o output -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("output1", "hello world$EOL")
        removeFile("output1")

        with(Split()) {
            parse(("input -o -").split(" ").toTypedArray())
            split()
        }
        assertFileContent("inputaa", "hello world$EOL")
        removeFile("inputaa")

        with(Split()) {
            parse(("input -o - -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("input1", "hello world$EOL")
        removeFile("input1")

        removeFile("input")
    }

    @Test
    fun testSplitsByLines() {
        createFile("input", "hello${EOL}world${EOL}How${EOL}are you?")

        with(Split()) {
            parse(("input -l 1").split(" ").toTypedArray())
            split()
        }
        assertFileContent("xaa", "hello$EOL")
        assertFileContent("xab", "world$EOL")
        assertFileContent("xac", "How$EOL")
        assertFileContent("xad", "are you?$EOL")
        removeFile("xaa")
        removeFile("xab")
        removeFile("xac")
        removeFile("xad")

        with(Split()) {
            parse(("input -l 2 -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("x1", "hello${EOL}world$EOL")
        assertFileContent("x2", "How${EOL}are you?$EOL")
        removeFile("x1")
        removeFile("x2")
    }

    @Test
    fun testSplitsByChars() {
        createFile("input", "hello world")

        with(Split()) {
            parse(("-c 4 input").split(" ").toTypedArray())
            split()
        }
        assertFileContent("xaa", "hell")
        assertFileContent("xab", "o wo")
        assertFileContent("xac", "rld")
        removeFile("xaa")
        removeFile("xab")
        removeFile("xac")

        with(Split()) {
            parse(("input -c 5 -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("x1", "hello")
        assertFileContent("x2", " worl")
        assertFileContent("x3", "d")
        removeFile("x1")
        removeFile("x2")
        removeFile("x3")

        with(Split()) {
            parse(("input -c 11 -d").split(" ").toTypedArray())
            split()
        }
        assertFileContent("x1", "hello world")
        removeFile("x1")


        with(Split()) {
            parse(("-c 1 -o - input").split(" ").toTypedArray())
            split()
        }
        assertFileContent("inputaa", "h")
        assertFileContent("inputab", "e")
        assertFileContent("inputac", "l")
        assertFileContent("inputad", "l")
        assertFileContent("inputae", "o")
        assertFileContent("inputaf", " ")
        assertFileContent("inputag", "w")
        assertFileContent("inputah", "o")
        assertFileContent("inputai", "r")
        assertFileContent("inputaj", "l")
        assertFileContent("inputak", "d")
        removeFile("inputaa")
        removeFile("inputab")
        removeFile("inputac")
        removeFile("inputad")
        removeFile("inputae")
        removeFile("inputaf")
        removeFile("inputag")
        removeFile("inputah")
        removeFile("inputai")
        removeFile("inputaj")
        removeFile("inputak")

        removeFile("input")
    }
}