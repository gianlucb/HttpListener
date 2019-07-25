# HttpListener

Java PoC of simple webserver.

As resources I used official Java Docs and tips from _StackOverflow_.
I also used [https://github.com/kkanellis/http-server/](https://github.com/kkanellis/http-server/) as reference code for the file/MIME handling part.
For sake of simplicity, this example accepts only **GET** and **HEAD** methods

## BUILD AND RUN

```bash
gradle build
```

```bash
gradle run --args="<PORT> <BASE_WEB_ROOT>"
```

Where _"PORT"_ is the listening port and _"BASE_WEB_ROOT"_ is the base folder where to serve files

For example on a Unix machine:

```bash
gradle run --args="8081 /home/user/wwwroot"
```

## TESTS

I implemented some basic unit tests that execute the webserver and try to download static content.
The tests also check if MIME handling is done correctly

```bash
gradle test
```
