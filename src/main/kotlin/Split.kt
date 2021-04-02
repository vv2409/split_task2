import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.io.BufferedReader
import java.io.File
import kotlin.math.ceil
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val utility = Split()
    utility.parse(args)
    utility.split()
}



class Split {

    val EOL: String = System.lineSeparator()

    private val defaultLines = 100

    @Option(name = "-o", required = false, usage = "базовое имя выходного файла")
    private var output: String? = null

    @Option(
        name = "-d",
        required = false,
        usage = "выходные файлы следует называть \"ofile1, ofile2, ofile3, ofile4 …\""
    )
    private var decimal: Boolean = false

    @Option(name = "-l", required = false, forbids = ["-c", "-n"], usage = "размер выходных файлов в строчках")
    private var linesInFile: Int? = null

    @Option(name = "-c", required = false, forbids = ["-l", "-n"], usage = "размер выходных файлов в символах")
    private var charsInFile: Int? = null

    @Option(name = "-n", required = false, forbids = ["-l", "-c"], usage = "количество выходных файлов")
    private var numFiles: Int? = null

    @Argument(required = true, usage = "имя входного файла")
    private lateinit var inputFile: File

    private lateinit var fillOutFiles: (reader: BufferedReader) -> Unit
    private var outUnits: Int = 0


    private lateinit var outFilenamePrefix: String
    private lateinit var outFilenameSuffix: FilenameSuffix

    fun parse(args: Array<String>) {
        val parser = CmdLineParser(this)
        parser.parseArgument(args.toList())

        if (!inputFile.exists()) {
            System.err.println("File $inputFile not found!")
            exitProcess(-1)
        }

        if (linesInFile == null && charsInFile == null && numFiles == null) {
            linesInFile = defaultLines
        } else if (linesInFile ?: 0 <= 0 && charsInFile ?: 0 <= 0 && numFiles ?: 0 <= 0) {
            System.err.println("Options' [-l | -c | -n] value must be positive!")
            exitProcess(-1)
        }


        outFilenameSuffix = if (decimal) {
            DecimalSuffix()
        } else {
            StringSuffix()
        }

        outFilenamePrefix = when (output) {
            null -> "x"
            "-" -> inputFile.nameWithoutExtension
            else -> output!!
        }

        fillOutFiles = when {
            numFiles != null -> {
                outUnits = ceil(inputFile.readText().length / numFiles!!.toDouble()).toInt()
                ::fillFileByChars
            }
            charsInFile != null -> {
                outUnits = charsInFile!!
                ::fillFileByChars
            }
            else -> {
                outUnits = linesInFile!!
                ::fillFileByLines
            }

        }

    }

    fun split() {
        fillOutFiles(inputFile.bufferedReader())
    }

    private fun fillFileByLines(reader: BufferedReader) {
        var currentLine = 0
        var outFileName = "$outFilenamePrefix${outFilenameSuffix.nextSuffix()}${if (inputFile.extension.isNotEmpty()) "." else ""}${inputFile.extension}"

        var writer = File(outFileName).writer()

        reader.forEachLine { line ->
            if (currentLine < outUnits) {
                writer.append("$line${EOL}")
                currentLine++
            } else {
                writer.close()
                outFileName = "$outFilenamePrefix${outFilenameSuffix.nextSuffix()}${if (inputFile.extension.isNotEmpty()) "." else ""}${inputFile.extension}"
                writer = File(outFileName).writer()
                writer.append("$line${EOL}")
                currentLine = 1
            }
        }
        writer.close()
    }


    private fun fillFileByChars(reader: BufferedReader) {
        var currentChar = 0

        var outFileName = "$outFilenamePrefix${outFilenameSuffix.nextSuffix()}${if (inputFile.extension.isNotEmpty()) "." else ""}${inputFile.extension}"
        var writer = File(outFileName).writer()

        var char = reader.read()
        while (char != -1) {
            if (currentChar < outUnits) {
                writer.append(char.toChar())
                currentChar++
            } else {
                writer.close()
                outFileName = "$outFilenamePrefix${outFilenameSuffix.nextSuffix()}${if (inputFile.extension.isNotEmpty()) "." else ""}${inputFile.extension}"
                writer = File(outFileName).writer()
                writer.append(char.toChar())
                currentChar = 1
            }

            char = reader.read()
        }

        reader.close()
        writer.close()
    }
}