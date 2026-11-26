// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "Playlists",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.3.0"),
    ],
    targets: [
        .target(name: "Playlists"),
        .testTarget(
            name: "PlaylistsTests",
            dependencies: ["Playlists", .product(name: "TableTest", package: "tabletest-swift")]
        ),
    ]
)
