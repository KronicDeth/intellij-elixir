package org.elixir_lang.status_bar_widget

import com.intellij.util.messages.Topic

/**
 * Listener for SDK refresh events. Used to notify the status widget
 * when SDKs have been refreshed and it should re-check their status.
 */
fun interface ElixirSdkRefreshListener {
    companion object {
        @JvmField
        val TOPIC: Topic<ElixirSdkRefreshListener> = Topic.create(
            "Elixir SDK Refreshed",
            ElixirSdkRefreshListener::class.java,
            Topic.BroadcastDirection.TO_CHILDREN
        )
    }

    fun sdksRefreshed()
}
