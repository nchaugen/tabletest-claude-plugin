// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "Daycare",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.1.2"),
    ],
    targets: [
        .target(name: "Daycare"),
        .testTarget(
            name: "DaycareTests",
            dependencies: ["Daycare", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
