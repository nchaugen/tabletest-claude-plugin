// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "SkiRental",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.1.2"),
    ],
    targets: [
        .target(name: "SkiRental"),
        .testTarget(
            name: "SkiRentalTests",
            dependencies: ["SkiRental", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
