workspace(
    name = "nl_tulipsolutions_bitcoin_tools",
)

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

# Load generated transitive maven dependencies using https://github.com/johnynek/bazel-deps
# Update dependencies.yaml
# bazel run //:parse -- generate --repo-root full/path/to/bitcoin-tools --sha-file third_party/workspace.bzl --deps third_party/dependencies.yaml
load("//third_party:workspace.bzl", "maven_dependencies")

maven_dependencies()

#
# Repositories
#

build_tools_version = "0.29.0"

http_archive(
    name = "com_github_bazelbuild_buildtools",
    sha256 = "05eb52437fb250c7591dd6cbcfd1f9b5b61d85d6b20f04b041e0830dd1ab39b3",
    strip_prefix = "buildtools-%s" % build_tools_version,
    url = "https://github.com/bazelbuild/buildtools/archive/%s.zip" % build_tools_version,
)

load("@com_github_bazelbuild_buildtools//buildifier:deps.bzl", "buildifier_dependencies")

buildifier_dependencies()

# Go is required for buildifier
io_bazel_rules_go_version = "v0.21.2"

http_archive(
    name = "io_bazel_rules_go",
    sha256 = "f99a9d76e972e0c8f935b2fe6d0d9d778f67c760c6d2400e23fc2e469016e2bd",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_go/releases/download/{v}/rules_go-{v}.tar.gz".format(v = io_bazel_rules_go_version),
        "https://github.com/bazelbuild/rules_go/releases/download/{v}/rules_go-{v}.tar.gz".format(v = io_bazel_rules_go_version),
    ],
)

load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")

go_rules_dependencies()

go_register_toolchains()

# Gazelle is required for buildifier
bazel_gazelle_version = "v0.19.1"

http_archive(
    name = "bazel_gazelle",
    sha256 = "86c6d481b3f7aedc1d60c1c211c6f76da282ae197c3b3160f54bd3a8f847896f",
    urls = [
        "https://storage.googleapis.com/bazel-mirror/github.com/bazelbuild/bazel-gazelle/releases/download/{v}/bazel-gazelle-{v}.tar.gz".format(v = bazel_gazelle_version),
        "https://github.com/bazelbuild/bazel-gazelle/releases/download/{v}/bazel-gazelle-{v}.tar.gz".format(v = bazel_gazelle_version),
    ],
)

load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies")

gazelle_dependencies()

version = "8ca948548159f288450516a09248dcfb9e957804"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = "05feb1d521a912f13a8d32a6aed0446b1876f577ece47edeec6e551a802b8b58",
    strip_prefix = "rules_kotlin-%s" % version,
    url = "https://github.com/bazelbuild/rules_kotlin/archive/%s.zip" % version,
)

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")

kotlin_repositories()

kt_register_toolchains()


