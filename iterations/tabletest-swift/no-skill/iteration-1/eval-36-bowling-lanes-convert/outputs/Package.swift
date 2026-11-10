// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "Bowling",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.1.2"),
    ],
    targets: [
        .target(name: "Bowling"),
        .testTarget(
            name: "BowlingTests",
            dependencies: ["Bowling", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
