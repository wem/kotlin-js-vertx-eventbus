rootProject.name = "kotlin-js-vertx-eventbus"
include("browser")
include("browser:core")
findProject(":browser:core")?.name = "core"
include("browser:protobuf")
findProject(":browser:protobuf")?.name = "protobuf"
include("browser:json")
findProject(":browser:json")?.name = "json"
include("browser:cbor")
findProject(":browser:cbor")?.name = "cbor"

include("server")
include("server:core")
findProject(":server:core")?.name = "core"
include("server:protobuf")
findProject(":server:protobuf")?.name = "protobuf"
include("server:json")
findProject(":server:json")?.name = "json"
include("server:cbor")
findProject(":server:cbor")?.name = "cbor"
