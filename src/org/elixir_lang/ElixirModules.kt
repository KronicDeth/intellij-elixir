package org.elixir_lang

import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.ResourceUtil
import com.intellij.util.io.URLUtil
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.util.*

object ElixirModules {
    fun add(parametersList: MutableList<String>, fileList: kotlin.collections.List<File>): MutableList<String> {
        for (file in fileList) {
            parametersList.add("-r")
            parametersList.add(file.path)
        }

        return parametersList
    }

    fun toRequireList(fileList: kotlin.collections.List<File>): kotlin.collections.List<String> =
            fileList.flatMap { file ->
                listOf("-r", file.path)
            }

    @Throws(IOException::class)
    private fun copy(basePath: String,
                     relativePathList: kotlin.collections.List<String>,
                     destinationDirectoryPath: String): kotlin.collections.List<File> {
        val destinationFileList = ArrayList<File>(relativePathList.size)

        for (relativePath in relativePathList) {
            destinationFileList.add(copy(basePath, relativePath, destinationDirectoryPath))
        }

        return destinationFileList
    }

    @Throws(IOException::class)
    private fun copy(basePath: String,
                     relativePath: String,
                     destinationDirectoryPath: String): File {
        val moduleUrl = ResourceUtil.getResource(ElixirModules::class.java, basePath, relativePath)
                ?: throw IOException(
                        "Failed to locate Elixir module (under base path `" + basePath + "` on relative path `" +
                                relativePath + "`"
                )

        return copy(moduleUrl, Paths.get(destinationDirectoryPath, basePath, relativePath).toFile())
    }

    @Throws(IOException::class)
    private fun copy(moduleURL: URL, destination: File): File {
        BufferedInputStream(URLUtil.openStream(moduleURL)).use { inputStream ->

            destination.parentFile.mkdirs()

            BufferedOutputStream(FileOutputStream(destination)).use { outputStream -> FileUtil.copy(inputStream, outputStream) }
        }

        return destination
    }

    @Throws(IOException::class)
    fun copy(basePath: String, relativePathList: kotlin.collections.List<String>): kotlin.collections.List<File> {
        val temporaryDirectory = FileUtil.createTempDirectory("intellij_elixir", null)
        return copy(basePath, relativePathList, temporaryDirectory.path)
    }

    fun parametersList(fileList: kotlin.collections.List<File>): kotlin.collections.List<String> = add(mutableListOf(), fileList)
}
