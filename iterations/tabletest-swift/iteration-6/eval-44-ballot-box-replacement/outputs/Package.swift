// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "Polling",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.1.2"),
    ],
    targets: [
        .target(name: "Polling"),
        .testTarget(
            name: "PollingTests",
            dependencies: ["Polling", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
