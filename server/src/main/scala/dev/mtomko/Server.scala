package dev.mtomko

import cats.effect._
import dev.mtomko.hello.EmuService
import higherkindness.mu.rpc.server._

object Server extends IOApp {

  implicit val targetResolver: EmuService[IO] = new RealEmuService[IO]

  def run(args: List[String]): IO[ExitCode] = for {
    serviceDef <- EmuService.bindService[IO]
    server     <- GrpcServer.default[IO](12345, List(AddService(serviceDef)))
    _          <- GrpcServer.server[IO](server)
  } yield ExitCode.Success

}
