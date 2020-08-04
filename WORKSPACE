workspace(
    name = "nl_tulipsolutions_bitcoin_tools",
)

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

http_archive(
    name = "nl_tulipsolutions_bazel_tools",
    strip_prefix = "tulip-bazel-tools-master",
    url = "https://github.com/TulipSolutions/tulip-bazel-tools/archive/master.zip",
)

RULES_JVM_EXTERNAL_TAG = "3.3"

RULES_JVM_EXTERNAL_SHA = "d85951a92c0908c80bd8551002d66cb23c3434409c814179c0ff026b53544dab"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

kotlin_compiler_version = "1.3.72"

maven_install(
    artifacts = [
        maven.artifact(
            "org.assertj",
            "assertj-core",
            "3.11.1",
            testonly = True,
        ),
        maven.artifact(
            "junit",
            "junit",
            "4.12",
            testonly = True,
        ),
        "org.bouncycastle:bcprov-jdk15on:1.64",
    ],
    maven_install_json = "//:maven_install.json",
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://repo.maven.apache.org/maven2/",
    ],
)

load("@maven//:defs.bzl", "pinned_maven_install")

pinned_maven_install()  # To upgrade pinned file run bazelisk run @unpinned_maven//:pin

rules_kotlin_version = "legacy-1.4.0-rc3"

rules_kotlin_sha = "da0e6e1543fcc79e93d4d93c3333378f3bd5d29e82c1bc2518de0dbe048e6598"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = rules_kotlin_sha,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/%s/rules_kotlin_release.tgz" % rules_kotlin_version],
)

KOTLIN_COMPILER_RELEASE = {
    "urls": [
        "https://github.com/JetBrains/kotlin/releases/download/v%s/kotlin-compiler-%s.zip" %
        (kotlin_compiler_version, kotlin_compiler_version),
    ],
    "sha256": "ccd0db87981f1c0e3f209a1a4acb6778f14e63fe3e561a98948b5317e526cc6c",
}

load("@io_bazel_rules_kotlin//kotlin:dependencies.bzl", "kt_download_local_dev_dependencies")

kt_download_local_dev_dependencies()

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")

kotlin_repositories(compiler_release = KOTLIN_COMPILER_RELEASE)  # if you want the default. Otherwise see custom kotlinc distribution below

kt_register_toolchains()  # to use the default toolchain, otherwise see toolchains below

## Formatters
load("@nl_tulipsolutions_bazel_tools//:go-deps.bzl", "tulip_bazel_tools_go_dependencies")

tulip_bazel_tools_go_dependencies()

load("@nl_tulipsolutions_bazel_tools//:go-setup.bzl", "tulip_bazel_tools_go_setup")

tulip_bazel_tools_go_setup()

load("@com_github_bazelbuild_buildtools//buildifier:deps.bzl", "buildifier_dependencies")

buildifier_dependencies()

load("@nl_tulipsolutions_bazel_tools//rules_intellij_formatter:deps.bzl", "intellij_formatter_dependencies")

intellij_formatter_dependencies()

load("@nl_tulipsolutions_bazel_tools//rules_ktlint:deps.bzl", "ktlint_dependencies")

ktlint_dependencies()

# Be aware depends on go deps from tulip_bazel_tools_go_setup
load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies", "go_repository")

gazelle_dependencies()

load("@nl_tulipsolutions_bazel_tools//rules_addlicense:deps.bzl", "addlicense_dependencies")

addlicense_dependencies()
