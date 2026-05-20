# Java HTTP Server

A small Java HTTP server built with plain sockets and manual request parsing.  
It listens on port `42069` and returns simple HTML responses based on the request path.

## Features

- Raw TCP socket server in Java
- Basic HTTP request parsing
- Simple response writer
- Separate internal packages for request, response, headers, and server logic
- Small PowerShell smoke test for request/response verification

## Project Structure

- `src/HTTPServerMain.java` - application entry point
- `src/internal/request/` - request parsing and request model classes
- `src/internal/response/` - HTTP response writing and status codes
- `src/internal/headers/` - header storage and parsing helpers
- `src/internal/server/` - socket server and request handler interface
- `compile.bat` - compiles the project into `out/`
- `run.bat` - starts the server
- `test-server.ps1` - sends raw HTTP requests and prints responses

## Requirements

- Java JDK installed and available on `PATH`
- PowerShell

## Build

Compile the project with:

```powershell
.\compile.bat
```

This creates class files in the `out/` directory.

## Run

Start the server with:

```powershell
.\run.bat
```

The server runs on:

```text
http://localhost:42069
```

## Test

First start the server, then run:

```powershell
.\test-server.ps1
```

The test script sends raw HTTP requests to the server, prints the request and response, and checks the returned status/body.

## Routes

- `/` - returns `200 OK` with `Hello World!`
- `/yourproblem` - returns `400 ERROR` with `400 - Your Problem`
- `/myproblem` - returns `500 INTERNAL SERVER ERROR` with `500 - My Problem`

## Notes

- The response format is intentionally simple.
- The project uses manual HTTP parsing instead of a framework.
