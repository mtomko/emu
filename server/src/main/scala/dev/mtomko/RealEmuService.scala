package dev.mtomko

import cats.effect.Sync
import cats.implicits._
import dev.mtomko.hello._

class RealEmuService[F[_]: Sync] extends EmuService[F] {

  def Resolve(req: Message): F[Message] = Sync[F].delay(println(s"Received: $req")) *> req.pure[F]

}
