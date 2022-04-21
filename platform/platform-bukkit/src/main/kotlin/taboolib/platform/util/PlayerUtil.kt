@file:Isolated

package taboolib.platform.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlot.*
import org.bukkit.inventory.ItemStack
import taboolib.common.Isolated
import taboolib.common.platform.function.adaptPlayer

fun Player.giveItem(itemStack: List<ItemStack>) {
    itemStack.forEach { giveItem(it) }
}

fun Player.giveItem(itemStack: ItemStack, repeat: Int = 1) {
    (1..repeat).forEach { _ ->
        inventory.addItem(itemStack).values.forEach { world.dropItem(location, it) }
    }
}

fun Player.getUsingItem(material: Material): ItemStack? {
    return when {
        itemInHand.type == material -> itemInHand
        inventory.itemInOffHand.type == material -> inventory.itemInOffHand
        else -> null
    }
}

fun Player.sendActionBar(message: String) {
    adaptPlayer(this).sendActionBar(message)
}

fun Player.feed() {
    foodLevel = 20
}

fun Player.saturate() {
    saturation = 20F
}