package io.github.sangcomz.diffhawk

import com.intellij.util.messages.Topic

interface SettingsChangeListener {
    fun onSettingsChanged()
    
    companion object {
        val TOPIC: Topic<SettingsChangeListener> = Topic.create("DiffHawk Settings Changed", SettingsChangeListener::class.java)
    }
}
