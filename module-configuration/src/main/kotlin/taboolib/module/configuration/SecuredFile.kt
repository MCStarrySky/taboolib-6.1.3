package taboolib.module.configuration

import taboolib.library.configuration.ConfigurationSection
import taboolib.library.configuration.InvalidConfigurationException
import taboolib.library.configuration.YamlConfiguration
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

class SecuredFile : YamlConfiguration() {

    private val lock = Any()

    override fun set(path: String, value: Any) {
        synchronized(lock) { super.set(path, value) }
    }

    override fun saveToString(): String {
        synchronized(lock) { return super.saveToString() }
    }

    /**
     * 如果文件读取失败则创建备份
     * 以防出现不可逆的损伤
     */
    @Throws(InvalidConfigurationException::class)
    override fun load(file: File) {
        val content = file.readText(StandardCharsets.UTF_8)
        try {
            loadFromString(content)
        } catch (ex: InvalidConfigurationException) {
            if (!file.name.endsWith(".bak")) {
                file.copyTo(File(file.parent, file.name + "_" + SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".bak"))
            }
            throw ex
        }
    }

    /**
     * 如果文本读取失败则打印到日志
     * 以防出现不可逆的损伤
     */
    @Throws(InvalidConfigurationException::class)
    override fun loadFromString(contents: String) {
        try {
            super.loadFromString(contents)
        } catch (t: InvalidConfigurationException) {
            println("Source: \n$contents")
            throw t
        }
    }

    companion object {

        fun dump(data: Any?): String {
            if (data == null) {
                return ""
            }
            var single = false
            val dump = YamlConfiguration()
            when (data) {
                is ConfigurationSection -> {
                    data.getValues(false).forEach { (path, value) -> dump[path] = value }
                }
                is Map<*, *> -> {
                    data.forEach { (k, v) -> dump[k.toString()] = v }
                }
                else -> {
                    single = true
                    dump["value"] = data
                }
            }
            val save = if (single) {
                dump.saveToString().substring("value:".length).trim { it <= ' ' }.split("\n").toTypedArray()
            } else {
                dump.saveToString().split("\n").toTypedArray()
            }
            return java.lang.String.join("\n", *save)
        }

        fun loadConfiguration(contents: String): SecuredFile {
            val config = SecuredFile()
            try {
                config.loadFromString(contents)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return config
        }

        fun loadConfiguration(file: File): SecuredFile {
            val config = SecuredFile()
            try {
                config.load(file)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return config
        }
    }
}