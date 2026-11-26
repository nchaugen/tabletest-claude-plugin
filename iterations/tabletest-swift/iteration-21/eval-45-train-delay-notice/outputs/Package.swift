// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "Departures",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.3.0"),
    ],
    targets: [
        .target(name: "Departures"),
        .testTarget(
            name: "DeparturesTests",
            dependencies: ["Departures", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
