package dev.mtomko

import cats.effect._
import cats.effect.Console.io._
import dev.mtomko.hello._
import dev.mtomko.hello.TargetType.TypeOne
import higherkindness.mu.rpc._

object Client extends IOApp {

  val channelFor: ChannelFor = ChannelForAddress("localhost", 12345)

  val serviceClient: Resource[IO, EmuService[IO]] = EmuService.client[IO](channelFor)

  val message: Message = Message("A target", Some(TypeOne))

  def run(args: List[String]): IO[ExitCode] =
    for {
      _        <- putStrLn(s"Sending $message")
      response <- serviceClient.use(c => c.Resolve(message))
      _        <- putStrLn(s"Received $response")
    } yield ExitCode.Success

}
