package dev.mtomko

import cats.effect._
import dev.mtomko.hello._
import dev.mtomko.hello.TargetType.TypeOne
import higherkindness.mu.rpc.testing.servers.withServerChannel
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext.Implicits

class ServerSpec extends AnyFlatSpec with TypeCheckedTripleEquals {

  val EC: scala.concurrent.ExecutionContext = Implicits.global

  implicit val timer: Timer[IO] = IO.timer(EC)
  implicit val cs: ContextShift[IO] = IO.contextShift(EC)

  implicit val service: EmuService[IO] = new RealEmuService[IO]

  /*
   * A cats-effect Resource that builds a gRPC server and client
   * connected to each other via an in-memory channel.
   */
  val clientResource: Resource[IO, EmuService[IO]] = for {
    sc        <- withServerChannel(EmuService.bindService[IO])
    clientRes <- EmuService.clientFromChannel[IO](IO.pure(sc.channel))
  } yield clientRes

  behavior.of("RPC server")

  it should "be happy" in {
    val response: Message = clientResource
      .use(client => client.Resolve(Message("somebody", Some(TypeOne))))
      .unsafeRunSync()

    assert(response.targetType === Some(TypeOne))
  }

}
