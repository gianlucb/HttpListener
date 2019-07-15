# HttpListener

Java PoC of simple webserver.

As resources I used official Java Docs and _StackOverflow_.
I also used [https://github.com/kkanellis/http-server/](https://github.com/kkanellis/http-server/) as reference code for the file/MIME handling part.

## BUILD AND RUN

```bash
gradle build
```

```bash
gradle run --args="<PORT> <BASE_WEB_ROOT>"
```

Where _<PORT>_ is the listening port and _<BASE_WEB_ROOT>_ is the base folder where to serve files

For example on a Windows machine

```bash
gradle run --args="8081 /home/user/wwwroot"
```

## TESTS

I implemented some basic unit tests

```bash
gradle test
```
