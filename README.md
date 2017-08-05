# Metenl

[![build status](https://gitlab.com/coolcfan/metenl/badges/master/build.svg)](https://gitlab.com/coolcfan/metenl/commits/master)

A simple and naive tool to automatically reply to /met message in super groups.

## Usage

1. Clone this repository and `cd` into the directory
2. Run `./gradlew shadowJar` to create an executable jar with dependencies bundled; you can find it in `./build/libs`
3. Create `metenl.conf` and set the following properties: `api.id`, `api.hash` and `met.username`
4. Run the jar :)

## Warning

Auto met loop issue not handled yet.