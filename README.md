# emu

# The problem:
When the client sends `Message("something", Some(TypeOne))` to the server, the server receives `Message("something", None)`.

To reproduce the problem you can either run `sbt server/test` or start up the server and client in separate `sbt` sessions:

```shell
$ sbt server/run
```

```shell
$ sbt client/run
```

From the client you'll see:
```shell
sbt:emu> client/run
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[info] Compiling 1 Scala source to /opt/broad/gpp/emu/client/target/scala-2.12/classes ...
[info] running dev.mtomko.Client
Sending Message(A target,Some(TypeOne))
Received Message(A target,None)
```

On the server side, you'll see:
```shell
sbt:emu> server/run
[info] running dev.mtomko.Server
Received: Message(A target,None)
```
