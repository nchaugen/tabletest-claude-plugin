// swift-tools-version:6.0
import PackageDescription

let package = Package(
    name: "HotelBooking",
    targets: [
        .target(name: "HotelBooking"),
        .testTarget(name: "HotelBookingTests", dependencies: ["HotelBooking"]),
    ]
)
