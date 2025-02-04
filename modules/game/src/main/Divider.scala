package lila.game

import com.github.blemale.scaffeine.Cache
import scala.concurrent.duration.*

import chess.Division
import chess.variant.Variant
import chess.format.Fen
import chess.format.pgn.SanStr

final class Divider:

  private val cache: Cache[GameId, Division] = lila.memo.CacheApi.scaffeineNoScheduler
    .expireAfterAccess(5 minutes)
    .build[GameId, Division]()

  def apply(game: Game, initialFen: Option[Fen.Epd]): Division =
    apply(game.id, game.sans, game.variant, initialFen)

  def apply(id: GameId, sans: => Vector[SanStr], variant: Variant, initialFen: Option[Fen.Epd]) =
    if (!Variant.list.divisionSensibleVariants(variant)) Division.empty
    else cache.get(id, _ => noCache(id, sans, variant, initialFen))

  def noCache(id: GameId, sans: Vector[SanStr], variant: Variant, initialFen: Option[Fen.Epd]) =
    chess.Replay
      .boards(
        sans = sans,
        initialFen = initialFen,
        variant = variant
      )
      .toOption
      .fold(Division.empty)(chess.Divider.apply)
