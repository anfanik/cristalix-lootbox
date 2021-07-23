package ru.cristalix.mod.lootbox

import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.registerHandler
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.V3

class Bootstrap : JavaMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        val crateScreen = CrateScreen()
        var ready = false
        var pressed = false

        registerHandler(GameLoop::class.java) {
            if (crateScreen.opened && Mouse.isButtonDown(0)) {
                crateScreen.opened = false
                crateScreen.close()
                return@registerHandler
            }

            if (ready && Mouse.isButtonDown(0) && !pressed) {
                pressed = true

                crateScreen.apply {
                    chest.animate(0.5, Easings.QUINT_OUT) {
                        chest.scale = V3(7.0, 7.0, 7.0)
                    }
                }

            }

            if (pressed) {
                clientApi.minecraft().setIngameNotInFocus()
                crateScreen.apply {
                    if (Mouse.isButtonDown(0)) shake()
                    else {
                        pressed = false
                        ready = false
                        open()
                        if (hasNextItem()) {
                            UIEngine.overlayContext.schedule(0.5) {
                                pressed = false
                                ready = true
                            }
                        }
                    }
                }
            }
        }

        registerHandler(MousePress::class.java) {
            isCancelled = ready
        }

        registerHandler(PluginMessage::class.java) {
            if (channel == "lootbox:open") {
                val amount = data.readInt()

                val loot: MutableList<Loot> = arrayListOf()
                for (i in 0 until amount) {
                    val item = ItemTools.read(data)
                    val name = NetUtil.readUtf8(data)
                    val rarity = when (NetUtil.readUtf8(data)) {
                        "NOTHING" -> NOTHING
                        "COMMON" -> COMMON
                        "UNCOMMON" -> UNCOMMON
                        "RARE" -> RARE
                        "EPIC" -> EPIC
                        "LEGENDARY" -> LEGENDARY
                        "INCREDIBLE" -> INCREDIBLE
                        else -> COMMON
                    }
                    loot.add(Loot(item, name, rarity))
                }

                clientApi.minecraft().setIngameNotInFocus()
                crateScreen.close()
                crateScreen.setup(loot)
                crateScreen.prepareToOpen()
                ready = true
            } else if (channel == "lootbox:close") {
                crateScreen.close()
                ready = false
            }
        }

        registerHandler(RenderTickPre::class.java) {
            crateScreen.apply {

                val intensity = rotationIntensity.color.alpha

                body1.animate(0.03) {
                    body1.rotation.degrees = (Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                body2.animate(0.03) {
                    body2.rotation.degrees = (Mouse.getY() / Display.getHeight().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                glowRect.animate(0.1) {
                    glowRect.rotation.degrees = -(Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
            }
        }


    }


}