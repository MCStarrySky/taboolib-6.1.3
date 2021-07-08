package taboolib.platform

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spongepowered.api.Sponge
import taboolib.common.OpenContainer
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformIO
import taboolib.common.platform.PlatformSide
import taboolib.platform.type.SpongeOpenContainer
import java.io.File

/**
 * TabooLib
 * taboolib.platform.SpongeIO
 *
 * @author sky
 * @since 2021/6/14 11:10 下午
 */
@Awake
@PlatformSide([Platform.SPONGE_API_8])
class SpongeIO : PlatformIO {

    private val logger: Logger
        get() = try {
            SpongePlugin.getInstance().pluginContainer.logger()
        } catch (ex: Exception) {
            LogManager.getLogger("Anonymous")
        }

    override val pluginId: String
        get() = SpongePlugin.getInstance().pluginContainer.metadata().id()

    override val isPrimaryThread: Boolean
        get() = Sponge.server().onMainThread()

    override fun info(vararg message: Any?) {
        message.filterNotNull().forEach { logger.info(it.toString()) }
    }

    override fun severe(vararg message: Any?) {
        message.filterNotNull().forEach { logger.error(it.toString()) }
    }

    override fun warning(vararg message: Any?) {
        message.filterNotNull().forEach { logger.warn(it.toString()) }
    }

    override fun releaseResourceFile(path: String, replace: Boolean): File {
        val file = File(SpongePlugin.getInstance().pluginConfigDir, path)
        if (file.exists() && !replace) {
            return file
        }
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        file.createNewFile()
        file.writeBytes(javaClass.classLoader.getResourceAsStream(path)?.readBytes() ?: error("resource not found: $path"))
        return file
    }

    override fun getJarFile(): File {
        return File(SpongePlugin::class.java.protectionDomain.codeSource.location.toURI().path)
    }

    override fun getDataFolder(): File {
        return SpongePlugin.getInstance().pluginConfigDir
    }

    override fun getOpenContainers(): List<OpenContainer> {
        return Sponge.pluginManager().plugins()
            .filter { it.instance()?.javaClass?.name?.endsWith("taboolib.platform.SpongePlugin") == true }
            .mapNotNull {
                try {
                    SpongeOpenContainer(it)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                    null
                }
            }
    }
}