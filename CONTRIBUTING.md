# Contributing to bitcoin-tools

## Tools

In order to build the project install the dependencies mentioned in the [README](README.md#usage-with-bazel).

## Building and running the examples

Run a full project build with the command:

    $ bazelisk build //...
    
The specific module can be tested with:

    $ bazelisk test //keyderivation/...    

## Generate build files

In case proto definitions change, are added or removed run Gazelle to regenerate the BUILD files.

    $ bazelisk run //:gazelle

## Linters

We use a couple of linters to format code, be sure to run them before submitting a change.

* [Ktlint](https://github.com/pinterest/ktlint) for Kotlin files (`bazelisk run //:ktlint_format`)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) (run headless) for many types of files
  (`bazelisk run //:intellij_format`)
* [addlicense](https://github.com/google/addlicense) for license headers many types of files
  (`bazelisk run //:addlicense_format`)
* [Buildifier](https://github.com/bazelbuild/buildtools) for `.bzl`, `BUILD.bazel` and `WORKSPACE` files
  (`bazelisk run //:buildifier_format`)