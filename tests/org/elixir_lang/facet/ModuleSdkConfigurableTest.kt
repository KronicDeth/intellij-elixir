package org.elixir_lang.facet

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.testFramework.common.runAll
import org.elixir_lang.Facet
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.elixir.ModuleSdkStatus
import org.elixir_lang.sdk.elixir.summaryHtml
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import java.awt.Component
import java.awt.Container
import javax.swing.JComboBox
import javax.swing.JLabel

/**
 * Tests the Settings → Elixir per-module SDK panel ([org.elixir_lang.facet.Configurable]): the SDK
 * chooser is populated, the status line under it renders the shared [ModuleSdkStatus] text, and
 * applying a selection writes the module's Facet SDK.
 */
class ModuleSdkConfigurableTest : PlatformTestCase() {

    private val added = mutableListOf<Sdk>()

    override fun setUp() {
        super.setUp()
        SdksService.getInstance()!!.resetForTests()
        ensureElixirFacet()
    }

    override fun tearDown() {
        runAll(
            {
                WriteAction.run<Throwable> {
                    FacetManager.getInstance(module).getFacetByType(Facet.ID)?.let { it.sdk = null }
                }
            },
            {
                WriteAction.run<Throwable> {
                    val table = ProjectJdkTable.getInstance()
                    added.filter { table.allJdks.contains(it) }.forEach { table.removeJdk(it) }
                }
                added.clear()
            },
            { SdksService.getInstance()!!.resetForTests() },
            { super.tearDown() },
        )
    }

    private fun ensureElixirFacet() {
        val facetManager = FacetManager.getInstance(module)
        if (facetManager.getFacetByType(Facet.ID) == null) {
            FacetUtil.addFacet(module, FacetType.findInstance(Type::class.java))
        }
    }

    private fun registerElixirSdk(name: String): Sdk {
        val sdk = ProjectJdkImpl(name, ElixirSdkType.instance)
        WriteAction.run<Throwable> { ProjectJdkTable.getInstance().addJdk(sdk) }
        added.add(sdk)
        SdksService.getInstance()!!.resetForTests()
        return sdk
    }

    private fun setFacetSdk(sdk: Sdk?) {
        WriteAction.run<Throwable> {
            FacetManager.getInstance(module).getFacetByType(Facet.ID)!!.sdk = sdk
        }
    }

    /** Concrete per-module configurable mirroring [org.elixir_lang.facet.configurable.Project]. */
    private fun moduleConfigurable(): Configurable = object : Configurable(module) {
        override fun initSdk(): Sdk? = Facet.sdk(module)
        override fun applySdk(sdk: Sdk?) = setFacetSdk(sdk)
    }

    private fun descendants(root: Component): List<Component> {
        val out = mutableListOf<Component>()
        fun visit(c: Component) {
            out.add(c)
            if (c is Container) c.components.forEach(::visit)
        }
        visit(root)
        return out
    }

    fun testChooserIsPopulated() {
        registerElixirSdk("Elixir Module Test A")

        val component = moduleConfigurable().createComponent()

        val combo = descendants(component).filterIsInstance<JComboBox<*>>().single()
        val names = (0 until combo.itemCount).mapNotNull { (combo.getItemAt(it) as? Sdk)?.name }
        assertTrue("chooser should contain the registered SDK; got $names", "Elixir Module Test A" in names)
    }

    fun testStatusLabelRendersSharedText() {
        val sdk = registerElixirSdk("Elixir Module Test B")
        setFacetSdk(sdk)

        val configurable = moduleConfigurable()
        val component = configurable.createComponent()
        configurable.reset()   // selects the module's Facet SDK

        val expected = "<html>${ModuleSdkStatus.of(sdk).summaryHtml()}</html>"
        val labelTexts = descendants(component).filterIsInstance<JLabel>().map { it.text }
        assertTrue(
            "status line should render the shared status text.\n  expected: $expected\n  labels:   $labelTexts",
            labelTexts.any { it == expected },
        )
    }

    fun testApplyWritesFacetSdk() {
        val sdk = registerElixirSdk("Elixir Module Test C")

        val configurable = moduleConfigurable()
        val component = configurable.createComponent()

        val combo = descendants(component).filterIsInstance<JComboBox<*>>().single()
        combo.selectedItem = SdksService.getInstance()!!.getModel().findSdk("Elixir Module Test C")
        configurable.apply()

        assertEquals("Elixir Module Test C", Facet.sdk(module)?.name)
    }
}
