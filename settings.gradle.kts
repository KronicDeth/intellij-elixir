plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    // Backend for the remote build cache below. A settings plugin cannot come from the version
    // catalog, so the version is a literal here; Dependabot reads it from this file.
    id("com.github.burrunan.s3-build-cache") version "1.9.9"
}

rootProject.name = "intellij-elixir"
include("jps-shared")
include("jps-builder")

// Remote build cache on Cloudflare R2 (bucket intellij-elixir-gh-cache). Which mode a build gets is
// decided by what the environment provides, so this one file serves every caller:
//
//   R2_ACCESS_KEY_ID + R2_SECRET_ACCESS_KEY set - main pushes, tag builds and pull requests from
//     branches in the upstream repository, the only contexts GitHub hands the secrets to: the S3
//     API, read and, on CI, push. Off CI the same credentials read only, so a developer holding an
//     "Object Read only" token gets hits without a local build ever polluting the shared cache.
//   GRADLE_REMOTE_CACHE_PUBLIC_URL set - fork pull requests, which get no secrets, and developers
//     who opt in: anonymous HTTP reads of the same objects, never push. R2's S3 endpoint demands
//     SigV4 even for a public bucket, so anonymous access has to go through the bucket's public
//     domain and Gradle's built-in HttpBuildCache. The plugin above stores each entry raw at
//     <prefix><cache key>, which is exactly the layout HttpBuildCache fetches.
//   neither - no remote cache, which is the default on a developer machine.
//
// The account id and bucket name are not secrets: without the token nothing can be read or written
// through the S3 endpoint, and the public domain is read-only by construction. The two secrets are
// a Cloudflare Account API token scoped to this bucket; see CONTRIBUTING.md, "Remote build cache".
val isCi: Boolean = System.getenv("CI") == "true"
val r2AccessKeyId: String = System.getenv("R2_ACCESS_KEY_ID").orEmpty()
val r2SecretAccessKey: String = System.getenv("R2_SECRET_ACCESS_KEY").orEmpty()
val remoteCachePublicUrl: String = System.getenv("GRADLE_REMOTE_CACHE_PUBLIC_URL").orEmpty()
// The account endpoint only. The SDK appends /<bucket>/<key> itself in path style.
val r2Endpoint = "https://fe128b0548b36adab8a3311011411092.r2.cloudflarestorage.com"
val remoteCacheBucket = "intellij-elixir-gh-cache"
// Bump to abandon every existing entry at once; the bucket's lifecycle rule expires the old prefix.
val remoteCachePrefix = "v1/"

buildCache {
    // On CI the local directory only ever cost a tarball in the GitHub Actions cache (see
    // .github/actions/setup-env/action.yml); the remote replaces it. Developers keep it.
    local {
        isEnabled = !isCi
    }
    when {
        r2AccessKeyId.isNotBlank() && r2SecretAccessKey.isNotBlank() ->
            remote<com.github.burrunan.s3cache.AwsS3BuildCache> {
                region = "auto"
                endpoint = r2Endpoint
                bucket = remoteCacheBucket
                prefix = remoteCachePrefix
                forcePathStyle = true
                // R2 implements the STANDARD and STANDARD_IA storage classes only; the plugin's
                // default asks for REDUCED_REDUNDANCY.
                isReducedRedundancy = false
                // Default is 50 MB. The root test task's results and reports can exceed that.
                maximumCachedObjectLength = 256L * 1024 * 1024
                awsAccessKeyId = r2AccessKeyId
                awsSecretKey = r2SecretAccessKey
                isPush = isCi
            }
        remoteCachePublicUrl.isNotBlank() ->
            remote<org.gradle.caching.http.HttpBuildCache> {
                url = java.net.URI(remoteCachePublicUrl)
                isPush = false
            }
    }
}
