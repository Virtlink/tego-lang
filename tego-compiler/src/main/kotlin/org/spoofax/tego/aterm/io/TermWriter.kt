package org.spoofax.tego.aterm.io

import org.spoofax.tego.aterm.Term
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.jvm.Throws

/**
 * A term writer.
 */
interface TermWriter {

    /**
     * Writes a term to a string.
     *
     * @param term the term to write
     * @return the string
     */
    @Throws(IOException::class)
    fun writeToString(term: Term): String
        = StringWriter().use { write(term, it) }.toString()

    /**
     * Writes a term to the specified path.
     *
     * @param term the term to write
     * @param path the path to write to
     * @param charset the character set to use
     */
    @Throws(IOException::class)
    fun write(term: Term, path: Path, charset: Charset = Charsets.UTF_8): Term
        = Files.newBufferedWriter(path, charset).use { write(term, it) }

    /**
     * Writes a term to the specified output stream.
     *
     * @param term the term to write
     * @param stream the output stream to write to
     * @param charset the character set to use
     */
    @Throws(IOException::class)
    fun write(term: Term, stream: OutputStream, charset: Charset = Charsets.UTF_8): Term
        = stream.bufferedWriter(charset).use { write(term, it) }

    /**
     * Writes a term to the specified writer.
     *
     * @param term the term to write
     * @param writer the writer to write to
     */
    @Throws(IOException::class)
    fun write(term: Term, writer: Writer): Term

}