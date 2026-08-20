package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresWriteLock
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.erlang_dependent.Type as ErlangDependentType

/**
 * Write-side SDK mutation commands for Elixir SDK additional data.
 *
 * Caller-managed threading: all methods require an already-held write lock.
 */
object ElixirSdkMutation {

    /**
     * Applies dependent Erlang SDK selection and optional warning-suppress flag update
     * in a single committed SDK mutation.
     *
     * @param preserveExistingWhenRequestedNull when true and [erlangSdk] is null, preserves an
     * existing dependent SDK reference instead of clearing it.
     */
    @RequiresWriteLock
    fun applyDependencySelection(
        elixirSdk: Sdk,
        erlangSdk: Sdk?,
        suppressOtpMismatchWarning: Boolean? = null,
        preserveExistingWhenRequestedNull: Boolean = false,
    ) {
        ThreadingAssertions.assertWriteAccess()
        mutateAdditionalData(elixirSdk, createIfMissing = true) { data, modificator ->
            if (!preserveExistingWhenRequestedNull || erlangSdk != null || data.getErlangSdkName() == null) {
                data.setErlangSdk(erlangSdk)
            }
            if (suppressOtpMismatchWarning != null) {
                data.setSuppressOtpMismatchWarning(suppressOtpMismatchWarning)
            }
            modificator.sdkAdditionalData = data
        }
        elixirSdk.putUserData(ErlangDependentType.ERLANG_SDK_KEY, erlangSdk)
    }

    /**
     * Persists suppress/unsuppress choice for OTP mismatch warning without changing SDK pairing.
     */
    @RequiresWriteLock
    fun setOtpMismatchWarningSuppressed(elixirSdk: Sdk, suppressed: Boolean) {
        ThreadingAssertions.assertWriteAccess()
        mutateAdditionalData(elixirSdk, createIfMissing = false) { data, modificator ->
            data.setSuppressOtpMismatchWarning(suppressed)
            modificator.sdkAdditionalData = data
        }
    }

    @RequiresWriteLock
    private fun mutateAdditionalData(
        elixirSdk: Sdk,
        createIfMissing: Boolean,
        mutate: (SdkAdditionalData, SdkModificator) -> Unit,
    ) {
        ThreadingAssertions.assertWriteAccess()

        val modificator = elixirSdk.sdkModificator
        val existing = (modificator.sdkAdditionalData as? SdkAdditionalData)
            ?: (elixirSdk.sdkAdditionalData as? SdkAdditionalData)

        val data = existing
            ?: if (createIfMissing) {
                SdkAdditionalData(null, elixirSdk)
            } else {
                return
            }

        mutate(data, modificator)
        modificator.commitChanges()
    }
}
