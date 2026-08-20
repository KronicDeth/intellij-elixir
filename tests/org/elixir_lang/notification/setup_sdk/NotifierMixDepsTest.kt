package org.elixir_lang.notification.setup_sdk

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import org.elixir_lang.PlatformTestCase

class NotifierMixDepsTest : PlatformTestCase() {
    override fun tearDown() {
        try {
            Notifier.clearMixDepsOutdated(project)
        } finally {
            super.tearDown()
        }
    }

    fun testMixDepsOutdated_sameRootDoesNotCreateAnotherNotification() {
        val root = myFixture.tempDirFixture.findOrCreateDir("project_a")

        capturedNotifications {
            Notifier.mixDepsOutdated(project, root)
        }
        val notifications = capturedNotifications {
            Notifier.mixDepsOutdated(project, root)
        }

        assertTrue(Notifier.hasActiveMixDepsOutdatedNotification(project))
        assertEquals(root.url, Notifier.activeMixDepsOutdatedRootUrl(project))
        assertTrue(
            "same root should not publish a second outdated notification",
            notifications.none { it.title.startsWith(Notifier.MIX_DEPS_OUTDATED_TITLE) },
        )
    }

    fun testMixDepsOutdated_differentRootRebindsNotification() {
        val rootA = myFixture.tempDirFixture.findOrCreateDir("project_a")
        val rootB = myFixture.tempDirFixture.findOrCreateDir("project_b")

        capturedNotifications {
            Notifier.mixDepsOutdated(project, rootA)
        }
        val notifications = capturedNotifications {
            Notifier.mixDepsOutdated(project, rootB)
        }

        assertTrue(Notifier.hasActiveMixDepsOutdatedNotification(project))
        assertEquals(rootB.url, Notifier.activeMixDepsOutdatedRootUrl(project))

        val outdated = notifications.lastOrNull { it.title.startsWith(Notifier.MIX_DEPS_OUTDATED_TITLE) }
        assertNotNull("expected rebound outdated notification", outdated)
        assertEquals("${Notifier.MIX_DEPS_OUTDATED_TITLE} (${rootB.name})", outdated!!.title)
    }

    private fun capturedNotifications(block: () -> Unit): List<Notification> {
        val captured = java.util.Collections.synchronizedList(mutableListOf<Notification>())
        val connection = project.messageBus.connect()
        try {
            connection.subscribe(Notifications.TOPIC, object : Notifications {
                override fun notify(notification: Notification) {
                    captured.add(notification)
                }
            })
            block()
        } finally {
            connection.disconnect()
        }

        return ArrayList(captured)
    }
}
