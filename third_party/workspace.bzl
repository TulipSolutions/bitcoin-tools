# Do not edit. bazel-deps autogenerates this file from third_party/dependencies.yaml.
def _jar_artifact_impl(ctx):
    jar_name = "%s.jar" % ctx.name
    ctx.download(
        output=ctx.path("jar/%s" % jar_name),
        url=ctx.attr.urls,
        sha256=ctx.attr.sha256,
        executable=False
    )
    src_name="%s-sources.jar" % ctx.name
    srcjar_attr=""
    has_sources = len(ctx.attr.src_urls) != 0
    if has_sources:
        ctx.download(
            output=ctx.path("jar/%s" % src_name),
            url=ctx.attr.src_urls,
            sha256=ctx.attr.src_sha256,
            executable=False
        )
        srcjar_attr ='\n    srcjar = ":%s",' % src_name

    build_file_contents = """
package(default_visibility = ['//visibility:public'])
java_import(
    name = 'jar',
    tags = ['maven_coordinates={artifact}'],
    jars = ['{jar_name}'],{srcjar_attr}
)
filegroup(
    name = 'file',
    srcs = [
        '{jar_name}',
        '{src_name}'
    ],
    visibility = ['//visibility:public']
)\n""".format(artifact = ctx.attr.artifact, jar_name = jar_name, src_name = src_name, srcjar_attr = srcjar_attr)
    ctx.file(ctx.path("jar/BUILD"), build_file_contents, False)
    return None

jar_artifact = repository_rule(
    attrs = {
        "artifact": attr.string(mandatory = True),
        "sha256": attr.string(mandatory = True),
        "urls": attr.string_list(mandatory = True),
        "src_sha256": attr.string(mandatory = False, default=""),
        "src_urls": attr.string_list(mandatory = False, default=[]),
    },
    implementation = _jar_artifact_impl
)

def jar_artifact_callback(hash):
    src_urls = []
    src_sha256 = ""
    source=hash.get("source", None)
    if source != None:
        src_urls = [source["url"]]
        src_sha256 = source["sha256"]
    jar_artifact(
        artifact = hash["artifact"],
        name = hash["name"],
        urls = [hash["url"]],
        sha256 = hash["sha256"],
        src_urls = src_urls,
        src_sha256 = src_sha256
    )
    native.bind(name = hash["bind"], actual = hash["actual"])


def list_dependencies():
    return [
    {"artifact": "junit:junit:4.12", "lang": "java", "sha1": "2973d150c0dc1fefe998f834810d68f278ea58ec", "sha256": "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/junit/junit/4.12/junit-4.12.jar", "source": {"sha1": "a6c32b40bf3d76eca54e3c601e5d1470c86fcdfa", "sha256": "9f43fea92033ad82bcad2ae44cec5c82abc9d6ee4b095cab921d11ead98bf2ff", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/junit/junit/4.12/junit-4.12-sources.jar"} , "name": "junit_junit", "actual": "@junit_junit//jar", "bind": "jar/junit/junit"},
    {"artifact": "org.assertj:assertj-core:3.11.1", "lang": "java", "sha1": "fdac3217b804d6900fa3650f17b5cb48e620b140", "sha256": "2ee2bd3e81fc818d423d442b658f28acf938d9078d6ba016a64b362fdd7779e8", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/assertj/assertj-core/3.11.1/assertj-core-3.11.1.jar", "source": {"sha1": "55362d8f04018b1140c5a370057bf8cd4a63c3de", "sha256": "a9e9c9ad5c8971a634d6681402174602888b09af3c1dd05bf41f3cc7441d2ae9", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/assertj/assertj-core/3.11.1/assertj-core-3.11.1-sources.jar"} , "name": "org_assertj_assertj_core", "actual": "@org_assertj_assertj_core//jar", "bind": "jar/org/assertj/assertj_core"},
    {"artifact": "org.bouncycastle:bcprov-jdk15on:1.64", "lang": "java", "sha1": "1467dac1b787b5ad2a18201c0c281df69882259e", "sha256": "a4f463ce552b908a722fa198ef4892a226b3225e453f8df10d5c0a5bfe5db6b6", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/bouncycastle/bcprov-jdk15on/1.64/bcprov-jdk15on-1.64.jar", "source": {"sha1": "2881bfaf2c15e9e64b62c2a143db90db7a0d6035", "sha256": "16b1e155644c76d307b803d822f7e996b6583677b623c5eb46baf10e915edda8", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/bouncycastle/bcprov-jdk15on/1.64/bcprov-jdk15on-1.64-sources.jar"} , "name": "org_bouncycastle_bcprov_jdk15on", "actual": "@org_bouncycastle_bcprov_jdk15on//jar", "bind": "jar/org/bouncycastle/bcprov_jdk15on"},
    {"artifact": "org.hamcrest:hamcrest-core:1.3", "lang": "java", "sha1": "42a25dc3219429f0e5d060061f71acb49bf010a0", "sha256": "66fdef91e9739348df7a096aa384a5685f4e875584cce89386a7a47251c4d8e9", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar", "source": {"sha1": "1dc37250fbc78e23a65a67fbbaf71d2e9cbc3c0b", "sha256": "e223d2d8fbafd66057a8848cc94222d63c3cedd652cc48eddc0ab5c39c0f84df", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3-sources.jar"} , "name": "org_hamcrest_hamcrest_core", "actual": "@org_hamcrest_hamcrest_core//jar", "bind": "jar/org/hamcrest/hamcrest_core"},
    ]

def maven_dependencies(callback = jar_artifact_callback):
    for hash in list_dependencies():
        callback(hash)
